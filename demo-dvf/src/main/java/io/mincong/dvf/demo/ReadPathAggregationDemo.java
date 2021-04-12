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
      runParisOverviewAnalysis(aggregator);
      runParisDistrictAnalysis(aggregator);
      runParisLotAnalysis(aggregator);
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

  public void runParisOverviewAnalysis(TransactionEsAggregator aggregator) {
    var overviewStats = aggregator.parisStatsOverview();
    logger.info("== Requesting analytics for Paris - Overview:");
    logger.info(
        "min: {}, avg: {}, max: {}, count: {}, sum: {}",
        String.format("%,.0f€", overviewStats.min),
        String.format("%,.0f€", overviewStats.avg),
        String.format("%,.0f€", overviewStats.max),
        String.format("%,d", overviewStats.count),
        String.format("%,.0f€", overviewStats.sum));
  }

  public void runParisDistrictAnalysis(TransactionEsAggregator aggregator) {
    logger.info("== Requesting analytics for Paris - Per Postal Code:");
    var percentilesArray = aggregator.parisPricePercentilesPerPostalCode();
    var totalPriceRows =
        percentilesArray.entrySet().stream()
            .map(
                entry -> {
                  var postalCode = entry.getKey();
                  var percentiles = entry.getValue();
                  return String.format(
                      "%s | %,.0f | %,.0f | %,.0f | %,.0f | %,.0f",
                      postalCode,
                      percentiles[0].percentile(5),
                      percentiles[0].percentile(25),
                      percentiles[0].percentile(50),
                      percentiles[0].percentile(75),
                      percentiles[0].percentile(95));
                })
            .collect(Collectors.joining("\n"));
    var totalPriceTable =
        "Postal Code | p5 (€) | p25 (€) | p50 (€) | p75 (€) | p95 (€)\n:---: | ---: | ---: | ---: | ---: | ---: |\n"
            + totalPriceRows;
    logger.info("Total Price Percentiles Per Postal Code in Paris\n{}", totalPriceTable);

    var m2PriceRows =
        percentilesArray.entrySet().stream()
            .map(
                entry -> {
                  var postalCode = entry.getKey();
                  var percentiles = entry.getValue();
                  return String.format(
                      "%s | %,.0f | %,.0f | %,.0f | %,.0f | %,.0f",
                      postalCode,
                      percentiles[1].percentile(5),
                      percentiles[1].percentile(25),
                      percentiles[1].percentile(50),
                      percentiles[1].percentile(75),
                      percentiles[1].percentile(95));
                })
            .collect(Collectors.joining("\n"));
    var m2PriceTable =
        "Postal Code | p5 (€/m2) | p25 (€/m2) | p50 (€/m2) | p75 (€/m2) | p95 (€/m2)\n:---: | ---: | ---: | ---: | ---: | ---: |\n"
            + m2PriceRows;
    logger.info("Price/M2 Percentiles Per Postal Code in Paris\n{}", m2PriceTable);
  }

  public void runParisLotAnalysis(TransactionEsAggregator aggregator) {
    logger.info("== Requesting analytics for Paris - Per Lot Type:");
    var percentilesArray = aggregator.parisPricePercentilesPerLotType();
    var totalPriceRows =
        percentilesArray.entrySet().stream()
            .map(
                entry -> {
                  var lotType = entry.getKey();
                  var percentiles = entry.getValue();
                  return String.format(
                      "%s | %,.0f | %,.0f | %,.0f | %,.0f | %,.0f",
                      lotType,
                      percentiles[0].percentile(5),
                      percentiles[0].percentile(25),
                      percentiles[0].percentile(50),
                      percentiles[0].percentile(75),
                      percentiles[0].percentile(95));
                })
            .collect(Collectors.joining("\n"));
    var totalPriceTable =
        "Lot Type | p5 (€) | p25 (€) | p50 (€) | p75 (€) | p95 (€)\n:---: | ---: | ---: | ---: | ---: | ---: |\n"
            + totalPriceRows;
    logger.info("Total Price Percentiles Per Lot Type in Paris\n{}", totalPriceTable);

    var m2PriceRows =
        percentilesArray.entrySet().stream()
            .map(
                entry -> {
                  var lotType = entry.getKey();
                  var percentiles = entry.getValue();
                  return String.format(
                      "%s | %,.0f | %,.0f | %,.0f | %,.0f | %,.0f",
                      lotType,
                      percentiles[1].percentile(5),
                      percentiles[1].percentile(25),
                      percentiles[1].percentile(50),
                      percentiles[1].percentile(75),
                      percentiles[1].percentile(95));
                })
            .collect(Collectors.joining("\n"));
    var m2PriceTable =
        "Lot Type | p5 (€/m2) | p25 (€/m2) | p50 (€/m2) | p75 (€/m2) | p95 (€/m2)\n:---: | ---: | ---: | ---: | ---: | ---: |\n"
            + m2PriceRows;
    logger.info("Price/M2 Percentiles Per Postal Code in Paris\n{}", m2PriceTable);
  }
}
