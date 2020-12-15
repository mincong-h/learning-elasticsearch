package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

public class TransactionEsWriter {

  static final String INDEX_NAME = "transactions";
  private static final Logger logger = LogManager.getLogger(TransactionEsWriter.class);

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;
  private final AtomicInteger counter;

  public TransactionEsWriter(RestHighLevelClient client) {
    this.client = client;
    this.objectMapper = Jackson.newObjectMapper();
    this.counter = new AtomicInteger(0);
  }

  public CompletableFuture<List<String>> write(Stream<ImmutableTransaction> transactions) {
    // TODO batch requests
    var ids =
        transactions.map(this::indexAsync).flatMap(Optional::stream).collect(Collectors.toList());
    return CompletableFuture.completedFuture(ids);
    //    return CompletableFuture.allOf(cfs.toArray(CompletableFuture[]::new))
    //        .thenApply(
    //            ignored -> {
    //              List<String> ids = new ArrayList<>();
    //              for (var cf : cfs) {
    //                if (cf.isDone()) {
    //                  ids.add(cf.join());
    //                }
    //              }
    //              return ids;
    //            });
  }

  public void createIndex() {
    var request = new CreateIndexRequest(INDEX_NAME).mapping(Transaction.esMappings());
    CreateIndexResponse response;
    try {
      response = client.indices().create(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create index " + INDEX_NAME, e);
    }
    if (!response.isAcknowledged()) {
      throw new IllegalStateException(
          "Failed to create index " + INDEX_NAME + ": response was not acknowledged");
    }
    logger.info("Creation of index {} is acknowledged", INDEX_NAME);
  }

  private Optional<String> indexAsync(ImmutableTransaction transaction) {
    logger.info("Indexing transaction {}: {}", counter.getAndIncrement(), transaction);
    String json;

    try {
      json = objectMapper.writeValueAsString(transaction);
    } catch (JsonProcessingException e) {
      logger.error("Transaction: FAILED", e);
      return Optional.empty();
    }

    var request = new IndexRequest(INDEX_NAME).source(json, XContentType.JSON);
    try {
      var response = client.index(request, RequestOptions.DEFAULT);
      logger.info("Transaction {}: OK", response.getId());
      return Optional.of(response.getId());
    } catch (IOException e) {
      logger.error("Transaction: FAILED", e);
      return Optional.empty();
    }
  }
}
