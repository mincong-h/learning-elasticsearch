package io.mincong.dvf.service;

import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms.ParsedBucket;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class TransactionEsSearcher {
  private static final Logger logger = LogManager.getLogger(TransactionEsSearcher.class);
  private final RestHighLevelClient client;

  public TransactionEsSearcher(RestHighLevelClient client) {
    this.client = client;
  }

  /**
   * Equivalent to HTTP request:
   *
   * <pre>
   * {
   *     "query": {
   *         "match_all": {}
   *     },
   *     "aggs": {
   *         "total": {
   *             "sum": {
   *                 "field": "${field_name}"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public Sum sumAggregate(String fieldName) {
    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.sum("total").field(fieldName))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    return (Sum) response.getAggregations().asMap().get("total");
  }

  public Map<String, Long> transactionByPostalCode(QueryBuilder queryBuilder) {
    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.terms("postal_code_agg").field("code_postal"))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation: " + sourceBuilder;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var terms = (ParsedStringTerms) response.getAggregations().asMap().get("postal_code_agg");
    return terms.getBuckets().stream()
        .map(b -> (ParsedBucket) b)
        .collect(
            Collectors.toMap(
                ParsedBucket::getKeyAsString,
                ParsedMultiBucketAggregation.ParsedBucket::getDocCount));
  }
}
