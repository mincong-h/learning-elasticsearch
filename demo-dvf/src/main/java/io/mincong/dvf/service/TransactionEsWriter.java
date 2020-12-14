package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class TransactionEsWriter {

  static final String INDEX_NAME = "transactions";
  private static final Logger logger = LogManager.getLogger(TransactionEsWriter.class);

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;

  public TransactionEsWriter(RestHighLevelClient client) {
    this.client = client;
    this.objectMapper = Jackson.newObjectMapper();
  }

  public CompletableFuture<List<String>> write(Stream<ImmutableTransaction> transactions) {
    // TODO batch requests
    var cfs = transactions.map(this::indexAsync).collect(Collectors.toList());
    return CompletableFuture.allOf(cfs.toArray(CompletableFuture[]::new))
        .thenApply(
            ignored -> {
              List<String> ids = new ArrayList<>();
              for (var cf : cfs) {
                if (cf.isDone()) {
                  ids.add(cf.join());
                }
              }
              return ids;
            });
  }

  private CompletableFuture<String> indexAsync(ImmutableTransaction transaction) {
    String json;

    try {
      json = objectMapper.writeValueAsString(transaction);
    } catch (JsonProcessingException e) {
      return CompletableFuture.failedFuture(e);
    }

    var cf = new CompletableFuture<IndexResponse>();
    var request = new IndexRequest(INDEX_NAME).source(json, XContentType.JSON);
    client.indexAsync(
        request,
        RequestOptions.DEFAULT,
        ActionListener.wrap(cf::complete, cf::completeExceptionally));
    return cf.thenApply(DocWriteResponse::getId)
        .whenComplete(
            (id, ex) -> {
              if (ex != null) {
                logger.error("Transaction " + id + ": FAILED", ex);
              } else {
                logger.info("Transaction {}: OK", id);
              }
            });
  }
}
