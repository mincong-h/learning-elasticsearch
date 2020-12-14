package io.mincong.dvf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mincong.dvf.model.ImmutableTransaction;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
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

  public CompletableFuture<Void> write(Stream<ImmutableTransaction> transactions) {
    // TODO batch requests
    var cfs = transactions.map(this::indexAsync).toArray(CompletableFuture[]::new);
    return CompletableFuture.allOf(cfs);
  }

  private CompletableFuture<IndexResponse> indexAsync(ImmutableTransaction transaction) {
    var cf = new CompletableFuture<IndexResponse>();
    String json;

    try {
      json = objectMapper.writeValueAsString(transaction);
    } catch (JsonProcessingException e) {
      cf.completeExceptionally(e);
      return cf;
    }

    var request = new IndexRequest(INDEX_NAME).id(transaction.id()).source(json, XContentType.JSON);
    client.indexAsync(
        request,
        RequestOptions.DEFAULT,
        ActionListener.wrap(cf::complete, cf::completeExceptionally));
    return cf.whenComplete(
        (response, ex) -> {
          if (ex != null) {
            logger.error("Transaction " + transaction.id() + ": FAILED", ex);
          } else {
            logger.info("Transaction " + transaction.id() + ": SUCCESSFUL");
          }
        });
  }
}
