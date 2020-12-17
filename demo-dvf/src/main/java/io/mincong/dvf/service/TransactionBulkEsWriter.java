package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

public class TransactionBulkEsWriter implements EsWriter {

  private static final Logger logger = LogManager.getLogger(TransactionBulkEsWriter.class);

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;
  private final AtomicInteger counter;
  private final RefreshPolicy refreshPolicy;
  private final Executor executor;
  private final String indexName;

  public TransactionBulkEsWriter(
      RestHighLevelClient client,
      String indexName,
      Executor executor,
      RefreshPolicy refreshPolicy) {
    this.client = client;
    this.indexName = indexName;
    this.objectMapper = Jackson.newObjectMapper();
    this.counter = new AtomicInteger(0);
    this.executor = executor;
    this.refreshPolicy = refreshPolicy;
  }

  @Override
  public CompletableFuture<List<String>> write(Stream<List<ImmutableTransaction>> transactions) {
    var cfs = transactions.map(this::indexAsync).collect(Collectors.toList());
    return CompletableFuture.allOf(cfs.toArray(CompletableFuture[]::new))
        .thenApply(
            ignored -> {
              List<String> ids = new ArrayList<>();
              for (var cf : cfs) {
                if (cf.isDone()) {
                  ids.addAll(cf.join());
                }
              }
              return ids;
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

  private CompletableFuture<List<String>> indexAsync(List<ImmutableTransaction> transactions) {
    return CompletableFuture.supplyAsync(() -> index(transactions), executor);
  }

  private List<String> index(List<ImmutableTransaction> transactions) {
    logger.info(
        "Indexing transaction {}: {}",
        counter.getAndIncrement(),
        transactions.stream()
            .map(ImmutableTransaction::mutationId)
            .collect(Collectors.joining(",")));

    var bulkRequest = new BulkRequest().setRefreshPolicy(refreshPolicy);

    for (var transaction : transactions) {
      try {
        var json = objectMapper.writeValueAsString(transaction);
        bulkRequest.add(new IndexRequest(indexName).source(json, XContentType.JSON));
      } catch (JsonProcessingException e) {
        // This should never happen
        throw new IllegalStateException("Failed to serialize transaction " + transaction, e);
      }
    }

    try {
      var response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      return Stream.of(response.getItems())
          .map(BulkItemResponse::getId)
          .collect(Collectors.toList());
    } catch (IOException e) {
      var msg =
          String.format(
              "Failed to index %d transactions: %s",
              transactions.size(),
              transactions.stream()
                  .map(ImmutableTransaction::mutationId)
                  .collect(Collectors.joining(",")));
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
  }
}
