package io.mincong.dvf.demo;

import io.mincong.dvf.service.TransactionEsAggregator;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;

public class ReadPathAggregationDemo {

  private static final Logger logger = LogManager.getLogger(ReadPathAggregationDemo.class);

  public void run() {
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    logger.info("Start creating REST high-level client...");
    try (var restClient = new RestHighLevelClient(builder)) {
      search(restClient);
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    }
  }

  public void search(RestHighLevelClient restClient) {
    var searcher = new TransactionEsAggregator(restClient);
    logger.info("Total property value: {}", searcher.sumAggregate("property_value").getValue());
    logger.info(
        "Transactions activity per postal code:\n{}",
        searcher.transactionByPostalCode(QueryBuilders.matchAllQuery()).entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n")));
  }
}
