package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

public class TransactionSimpleEsWriter implements EsWriter {

  private static final Logger logger = LogManager.getLogger(TransactionSimpleEsWriter.class);

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;
  private final AtomicInteger counter;
  private final RefreshPolicy refreshPolicy;
  private final String indexName;

  public TransactionSimpleEsWriter(
      RestHighLevelClient client, String indexName, RefreshPolicy refreshPolicy) {
    this.client = client;
    this.indexName = indexName;
    this.objectMapper = Jackson.newObjectMapper();
    this.counter = new AtomicInteger(0);
    this.refreshPolicy = refreshPolicy;
  }

  @Override
  public CompletableFuture<Long> write(Stream<List<ImmutableTransaction>> transactions) {
    var cfs = transactions.flatMap(List::stream).map(this::index).collect(Collectors.toList());
    return CompletableFuture.allOf(cfs.toArray(CompletableFuture[]::new))
        .thenApply(
            ignored -> {
              List<String> ids = new ArrayList<>();
              for (var cf : cfs) {
                if (cf.isDone()) {
                  ids.addAll(cf.join());
                }
              }
              return (long) ids.size();
            });
  }

  @Override
  public void createIndex() {
    var request = new CreateIndexRequest(indexName).mapping(Transaction.esMappings());
    CreateIndexResponse response;
    try {
      response = client.indices().create(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create index " + indexName, e);
    }
    if (!response.isAcknowledged()) {
      throw new IllegalStateException(
          "Failed to create index " + indexName + ": response was not acknowledged");
    }
    logger.info("Creation of index {} is acknowledged", indexName);
  }

  private CompletableFuture<List<String>> index(ImmutableTransaction transaction) {
    logger.info("Indexing transaction {}: {}", counter.getAndIncrement(), transaction.mutationId());

    String json;
    try {
      json = objectMapper.writeValueAsString(transaction);
    } catch (JsonProcessingException e) {
      // This should never happen
      logger.error("Failed to serialize transaction " + transaction, e);
      return CompletableFuture.failedFuture(e);
    }
    var request =
        new IndexRequest(indexName).source(json, XContentType.JSON).setRefreshPolicy(refreshPolicy);
    try {
      var response = client.index(request, RequestOptions.DEFAULT);
      return CompletableFuture.completedFuture(List.of(response.getId()));
    } catch (IOException e) {
      logger.error("Transaction: FAILED", e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
