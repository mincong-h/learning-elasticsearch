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

public class ReadPathAggregationDemo {

  private static final Logger logger = LogManager.getLogger(ReadPathAggregationDemo.class);

  public void run() {
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    logger.info("Start creating REST high-level client...");
    try (var restClient = new RestHighLevelClient(builder)) {
      var aggregator = new TransactionEsAggregator(restClient);
      runMetricAggregations(aggregator);
      runBucketAggregations(aggregator);
      runMetricScriptingStatsAggregations(aggregator);
      runParisAnalysis(aggregator);
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    }
  }

  public void runMetricAggregations(TransactionEsAggregator aggregator) {
    var count = aggregator.mutationIdsCount().getValue();
    logger.info("== Requesting single metric aggregation:");
    logger.info("Number of mutations: {}", String.format("%,d", count));

    var stats = aggregator.aggregations();
    logger.info("== Requesting multiple metric aggregations:");
    logger.info(
        "Property values are between {} and {} (avg: {})",
        String.format("%,.1f€", stats.min),
        String.format("%,.1f€", stats.max),
        String.format("%,.1f€", stats.avg));
    logger.info(
        "Property values total market value is {} ({} transactions)",
        String.format("%,.1f€", stats.sum),
        String.format("%,d", stats.count));
  }

  public void runMetricScriptingStatsAggregations(TransactionEsAggregator aggregator) {
    var stats = aggregator.priceStats();
    logger.info("== Requesting analytics for price/m2 - overview:");
    logger.info(
        "Property values are between {} and {} (avg: {})",
        String.format("%,.1f€", stats.min),
        String.format("%,.1f€", stats.max),
        String.format("%,.1f€", stats.avg));
    logger.info(
        "Property values total market value is {} ({} transactions)",
        String.format("%,.1f€", stats.sum),
        String.format("%,d", stats.count));
  }

  public void runParisAnalysis(TransactionEsAggregator aggregator) {
    var overviewStats = aggregator.parisStatsOverview();
    logger.info("== Requesting analytics for Paris - Overview:");
    logger.info(
        "Property values are between {} and {} (avg: {})",
        String.format("%,.1f€", overviewStats.min),
        String.format("%,.1f€", overviewStats.max),
        String.format("%,.1f€", overviewStats.avg));
    logger.info(
        "Property values total market value is {} ({} transactions)",
        String.format("%,.1f€", overviewStats.sum),
        String.format("%,d", overviewStats.count));

    logger.info("== Requesting analytics for Paris - Per Postal Code:");
    var statsPerPostalCode = aggregator.parisStatsPerPostalCode();
    statsPerPostalCode.forEach(
        (postalCode, stats) -> {
          logger.info(
              "- {}: property values are between {} and {} (avg: {})",
              postalCode,
              String.format("%,.1f€", stats.min),
              String.format("%,.1f€", stats.max),
              String.format("%,.1f€", stats.avg));
        });
  }

  private void runBucketAggregations(TransactionEsAggregator aggregator) {
    logger.info("== Requesting bucket aggregations:");
    logger.info(
        "Transactions activity per postal code:\n{}",
        aggregator.mutationsByPostalCode().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n")));
  }
}
