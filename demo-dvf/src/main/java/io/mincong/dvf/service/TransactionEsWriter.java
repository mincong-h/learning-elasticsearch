package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

public class TransactionEsWriter {

  private static final Logger logger = LogManager.getLogger(TransactionEsWriter.class);

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;
  private final AtomicInteger counter;
  private final RefreshPolicy refreshPolicy;

  public TransactionEsWriter(RestHighLevelClient client, RefreshPolicy refreshPolicy) {
    this.client = client;
    this.objectMapper = Jackson.newObjectMapper();
    this.counter = new AtomicInteger(0);
    this.refreshPolicy = refreshPolicy;
  }

  public CompletableFuture<List<String>> write(ImmutableTransaction... transactions) {
    return write(Stream.of(List.of(transactions)));
  }

  public CompletableFuture<List<String>> write(Stream<List<ImmutableTransaction>> transactions) {
    // TODO batch requests
    var ids = transactions.map(this::indexAsync).flatMap(List::stream).collect(Collectors.toList());
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
    var request = new CreateIndexRequest(Transaction.INDEX_NAME).mapping(Transaction.esMappings());
    CreateIndexResponse response;
    try {
      response = client.indices().create(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create index " + Transaction.INDEX_NAME, e);
    }
    if (!response.isAcknowledged()) {
      throw new IllegalStateException(
          "Failed to create index " + Transaction.INDEX_NAME + ": response was not acknowledged");
    }
    logger.info("Creation of index {} is acknowledged", Transaction.INDEX_NAME);
  }

  // FIXME this is too slow
  private List<String> indexAsync(List<ImmutableTransaction> transactions) {
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
        bulkRequest.add(new IndexRequest(Transaction.INDEX_NAME).source(json, XContentType.JSON));
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
      logger.error("Transaction: FAILED", e);
      return List.of();
    }
  }
}
