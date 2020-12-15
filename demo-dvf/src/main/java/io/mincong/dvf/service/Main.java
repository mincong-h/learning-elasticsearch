package io.mincong.dvf.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;

public class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);
  private static final String CSV_PATH = "/Volumes/Samsung_T5/dvf/downloads/full.2020.csv";

  public static void main(String[] args) {
    var main = new Main();
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    logger.info("Start creating REST high-level client...");
    try (var restClient = new RestHighLevelClient(builder)) {
      //      main.indexTransactions(restClient).join();
      main.search(restClient);
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    }
  }

  public CompletableFuture<?> indexTransactions(RestHighLevelClient restClient) {
    var start = Instant.now();
    var csvReader = new TransactionCsvReader();
    var esWriter = new TransactionEsWriter(restClient, RefreshPolicy.NONE);

    var transactions = csvReader.readCsv(Path.of(CSV_PATH)).limit(1_000); // total: 827,106
    esWriter.createIndex();
    logger.info("Start writing transaction...");
    return esWriter
        .write(transactions)
        .whenComplete(
            (ids, ex) -> {
              if (ex != null) {
                logger.error("Failed to complete", ex);
              } else {
                var duration = Duration.between(start, Instant.now());
                logger.info("Finished, indexed {} documents in {}", ids.size(), duration);
              }
            });
  }

  public void search(RestHighLevelClient restClient) {
    var searcher = new TransactionEsSearcher(restClient);
    logger.info("Total property value: {}", searcher.sumAggregate("property_value").getValue());
    logger.info(
        "Transactions activity per postal code:\n{}",
        searcher.transactionByPostalCode(QueryBuilders.matchAllQuery()).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n")));
  }
}
