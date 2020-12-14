package io.mincong.dvf.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);
  private static final String CSV_PATH = "/Users/mincong/github/dvf/downloads/full.2020.csv";

  public static void main(String[] args) {
    var main = new Main();
    // FIXME ERROR StatusLogger Log4j2 could not find a logging implementation.
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    try (var restClient = new RestHighLevelClient(builder)) {
      main.run(restClient).join();
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    }
  }

  public CompletableFuture<?> run(RestHighLevelClient restClient) {
    var csvReader = new TransactionCsvReader();
    var esWriter = new TransactionEsWriter(restClient);
    var transactions = csvReader.readCsv(Path.of(CSV_PATH)).limit(100);

    return esWriter
        .createIndex()
        .thenCompose(ignored -> esWriter.write(transactions))
        .whenComplete(
            (ids, ex) -> {
              if (ex != null) {
                logger.error("Failed to complete", ex);
              } else {
                logger.info("Finished, indexed {} documents", ids);
              }
            });
  }
}
