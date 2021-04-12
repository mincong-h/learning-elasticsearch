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
      runMetricScriptingPercentilesAggregations(aggregator);
      runParisAnalysis(aggregator);
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    }
  }

  public void runMetricAggregations(TransactionEsAggregator aggregator) {
    var count = aggregator.mutationIdsCount().getValue();
    logger.info("== Requesting single metric aggregation:");
    logger.info("Number of mutations: {}", String.format("%,d", count));
  }

  private void runBucketAggregations(TransactionEsAggregator aggregator) {
    logger.info("== Requesting bucket aggregation:");
    logger.info(
        "Transactions activity per postal code:\n{}",
        aggregator.mutationsByPostalCode().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining("\n")));
  }

  public void runMetricScriptingStatsAggregations(TransactionEsAggregator aggregator) {
    var stats = aggregator.priceM2Stats();
    logger.info("== Requesting analytics for price/m2 - Overview:");
    logger.info(
        "Property values are between {} and {} (avg: {})",
        String.format("%,.1f€/m2", stats.min),
        String.format("%,.1f€/m2", stats.max),
        String.format("%,.1f€/m2", stats.avg));
    logger.info("There were {} mutations", String.format("%,d", stats.count));
  }

  public void runMetricScriptingPercentilesAggregations(TransactionEsAggregator aggregator) {
    var stats = aggregator.priceM2Percentiles();
    logger.info("== Requesting analytics for price/m2 - Percentiles:");
    logger.info("p5: {}", String.format("%,.0f€/m2", stats.percentile(5.0)));
    logger.info("p25: {}", String.format("%,.0f€/m2", stats.percentile(25.0)));
    logger.info("p50: {}", String.format("%,.0f€/m2", stats.percentile(50.0)));
    logger.info("p75: {}", String.format("%,.0f€/m2", stats.percentile(75.0)));
    logger.info("p95: {}", String.format("%,.0f€/m2", stats.percentile(95.0)));
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
}
