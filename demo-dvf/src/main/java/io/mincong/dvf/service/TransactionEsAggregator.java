package io.mincong.dvf.service;

import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms.ParsedBucket;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * Elasticsearch - Aggregations
 * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/search-aggregations.html
 *
 * <p>Elasticsearch organizes aggregations into three categories:
 *
 * <ul>
 *   <li>Metric aggregations that calculate metrics, such as a sum or average, from field values.
 *   <li>Bucket aggregations that group documents into buckets, also called bins, based on field
 *       values, ranges, or other criteria.
 *   <li>Pipeline aggregations that take input from other aggregations instead of documents or
 *       fields.
 * </ul>
 */
public class TransactionEsAggregator {
  private static final Logger logger = LogManager.getLogger(TransactionEsAggregator.class);
  private final RestHighLevelClient restClient;

  public TransactionEsAggregator(RestHighLevelClient restClient) {
    this.restClient = restClient;
  }

  /**
   * Equivalent to HTTP request:
   *
   * <pre>
   * GET /transactions/_search
   *
   * {
   *     "query": {
   *         "match_all": {}
   *     },
   *     "size": 0,
   *     "aggs": {
   *         "mutation_id/value_count": {
   *             "value_count": {
   *                 "field": "mutation_id"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public ValueCount mutationIdsCount() {
    var fieldName = Transaction.FIELD_MUTATION_ID;
    var aggregationName = fieldName + "/value_count";
    var sourceBuilder =
        new SearchSourceBuilder()
            .size(0)
            .aggregation(AggregationBuilders.count(aggregationName).field(fieldName))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    try {
      var response = restClient.search(request, RequestOptions.DEFAULT);
      return response.getAggregations().get(aggregationName);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
  }

  /**
   * Equivalent to HTTP request:
   *
   * <pre>
   * {
   *   "query": {
   *     "bool": {
   *       "filter": [
   *         { "match": { "mutation_nature": { "query": "Vente" } } },
   *         { "match": { "local_type": { "query": "Appartement" } } },
   *         { "range": { "property_value": { "gt": 0 } } },
   *         { "range": { "real_built_up_area": { "gt": 0 } } }
   *       ]
   *     }
   *   },
   *   "runtime_mappings": {
   *     "price_m2": {
   *       "type": "double",
   *       "script": "emit(doc['property_value'].value / doc['real_built_up_area'].value)"
   *     }
   *   },
   *   "size": 0,
   *   "aggs": {
   *     "price_m2/stats": {
   *       "stats": {
   *         "field": "price_m2"
   *       }
   *     }
   *   }
   * }
   * </pre>
   */
  public Stats priceM2Stats() {
    var fieldName = "price_m2";
    var statsAggregationName = fieldName + "/stats";

    var mutationNatureQuery = QueryBuilders.matchQuery(Transaction.FIELD_MUTATION_NATURE, "Vente");
    var localTypeQuery = QueryBuilders.matchQuery(Transaction.FIELD_LOCAL_TYPE, "Appartement");

    // Add queries to avoid script exception (error 400) in painless script:
    // "A document doesn't have a value for a field! Use doc[<field>].size()==0 to check if a
    // document is missing a field!"
    var propertyValueQuery = QueryBuilders.rangeQuery(Transaction.FIELD_PROPERTY_VALUE).gt(0);
    var propertyBuiltUpAreaQuery =
        QueryBuilders.rangeQuery(Transaction.FIELD_REAL_BUILT_UP_AREA).gt(0);
    var query =
        QueryBuilders.boolQuery()
            .filter(mutationNatureQuery)
            .filter(localTypeQuery)
            .filter(propertyValueQuery)
            .filter(propertyBuiltUpAreaQuery);

    Map<String, Object> runtimeMappings =
        Map.of(
            fieldName,
            Map.of(
                "type",
                "double",
                "script",
                "emit(doc['property_value'].value / doc['real_built_up_area'].value)"));
    var sourceBuilder =
        new SearchSourceBuilder()
            .runtimeMappings(runtimeMappings)
            .aggregation(AggregationBuilders.stats(statsAggregationName).field(fieldName))
            .size(0)
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    try {
      var response = restClient.search(request, RequestOptions.DEFAULT);
      return response.getAggregations().get(statsAggregationName);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
  }

  /**
   * Equivalent to HTTP request:
   *
   * <pre>
   * {
   *     "query": {
   *         "match": {
   *             "mutation_nature": {
   *                 "query": "Vente"
   *             }
   *         },
   *         "match": {
   *             "local_type": {
   *                 "query": "Appartement"
   *             }
   *         },
   *         "range": {
   *             "property_value": {
   *                 "gt": 0
   *             }
   *         },
   *         "range": {
   *             "real_built_up_area": {
   *                 "gt": 0
   *             }
   *         }
   *     },
   *     "runtime_mappings": {
   *         "price_m2": {
   *             "type": "double",
   *             "script": "emit(doc['property_value'].value / doc['real_built_up_area'].value)"
   *         }
   *     },
   *     "aggs": {
   *         "price_m2/outlier": {
   *             "percentiles": {
   *                 "field": "price_m2"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public Percentiles priceM2Percentiles() {
    var fieldName = "price_m2";
    var percentilesAggregationName = fieldName + "/percentiles";

    var mutationNatureQuery = QueryBuilders.matchQuery(Transaction.FIELD_MUTATION_NATURE, "Vente");
    var localTypeQuery = QueryBuilders.matchQuery(Transaction.FIELD_LOCAL_TYPE, "Appartement");

    // Add queries to avoid script exception (error 400) in painless script:
    // "A document doesn't have a value for a field! Use doc[<field>].size()==0 to check if a
    // document is missing a field!"
    var propertyValueQuery = QueryBuilders.rangeQuery(Transaction.FIELD_PROPERTY_VALUE).gt(0);
    var propertyBuiltUpAreaQuery =
        QueryBuilders.rangeQuery(Transaction.FIELD_REAL_BUILT_UP_AREA).gt(0);
    var query =
        QueryBuilders.boolQuery()
            .filter(mutationNatureQuery)
            .filter(localTypeQuery)
            .filter(propertyValueQuery)
            .filter(propertyBuiltUpAreaQuery);

    Map<String, Object> runtimeMappings =
        Map.of(
            fieldName,
            Map.of(
                "type",
                "double",
                "script",
                "emit(doc['property_value'].value / doc['real_built_up_area'].value)"));
    var sourceBuilder =
        new SearchSourceBuilder()
            .runtimeMappings(runtimeMappings)
            .aggregation(
                AggregationBuilders.percentiles(percentilesAggregationName).field(fieldName))
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = restClient.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    return response.getAggregations().get(percentilesAggregationName);
  }

  /** See request.paris.price-stats-overview.json */
  public Stats parisStatsOverview() {
    var fieldName = Transaction.FIELD_PROPERTY_VALUE;
    var statsAggregationName = fieldName + "/stats";

    var postalCodeQuery = QueryBuilders.wildcardQuery(Transaction.FIELD_POSTAL_CODE, "75*");
    var mutationNatureQuery = QueryBuilders.matchQuery(Transaction.FIELD_MUTATION_NATURE, "Vente");
    var localTypeQuery = QueryBuilders.matchQuery(Transaction.FIELD_LOCAL_TYPE, "Appartement");
    var query =
        QueryBuilders.boolQuery()
            .filter(postalCodeQuery)
            .filter(mutationNatureQuery)
            .filter(localTypeQuery);

    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.stats(statsAggregationName).field(fieldName))
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = restClient.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    return response.getAggregations().get(statsAggregationName);
  }

  /**
   * This is a sub-aggregation. See
   * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/search-aggregations.html
   *
   * @return a map of stats where the key is the postal code and the value is its related
   *     percentiles for total property value and percentiles for price per m2.
   */
  public SortedMap<String, Percentiles[]> parisPricePercentilesPerPostalCode() {
    var termsAggregationName = "postal-code-aggregation";

    var totalPriceField = Transaction.FIELD_PROPERTY_VALUE;
    var totalPriceAggregation = totalPriceField + "/percentiles";

    var m2PriceField = "price_m2";
    var m2PriceAggregation = m2PriceField + "/percentiles";

    var postalCodeQuery = QueryBuilders.wildcardQuery(Transaction.FIELD_POSTAL_CODE, "75*");
    var mutationNatureQuery = QueryBuilders.matchQuery(Transaction.FIELD_MUTATION_NATURE, "Vente");
    var localTypeQuery = QueryBuilders.matchQuery(Transaction.FIELD_LOCAL_TYPE, "Appartement");
    var query =
        QueryBuilders.boolQuery()
            .filter(postalCodeQuery)
            .filter(mutationNatureQuery)
            .filter(localTypeQuery);

    Map<String, Object> runtimeMappings =
        Map.of(
            "price_m2",
            Map.of(
                "type",
                "double",
                "script",
                "emit(doc['property_value'].value / doc['real_built_up_area'].value)"));

    var termsAggregation =
        AggregationBuilders.terms(termsAggregationName)
            .field(Transaction.FIELD_POSTAL_CODE)
            .size(20)
            .subAggregation(
                AggregationBuilders.percentiles(totalPriceAggregation).field(totalPriceField))
            .subAggregation(
                AggregationBuilders.percentiles(m2PriceAggregation).field(m2PriceField));

    var sourceBuilder =
        new SearchSourceBuilder()
            .runtimeMappings(runtimeMappings)
            .aggregation(termsAggregation)
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = restClient.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + totalPriceField;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var terms = (ParsedStringTerms) response.getAggregations().get(termsAggregationName);
    return terms.getBuckets().stream()
        .collect(
            Collectors.toMap(
                MultiBucketsAggregation.Bucket::getKeyAsString,
                bucket -> toPercentilesArray(bucket.getAggregations()),
                (k1, k2) -> k1,
                TreeMap::new));
  }

  private Percentiles[] toPercentilesArray(Aggregations aggregations) {
    Percentiles totalPricePercentiles = aggregations.get("property_value/percentiles");
    Percentiles m2PricePercentiles = aggregations.get("price_m2/percentiles");
    return new Percentiles[] {totalPricePercentiles, m2PricePercentiles};
  }
  /**
   * This is a sub-aggregation. See
   * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/search-aggregations.html
   *
   * @return a map of stats where the key is the custom lot-type (T1, T2, ...) and the value is its
   *     related percentiles for total property value and percentiles for price per m2.
   */
  public SortedMap<String, Percentiles[]> parisPricePercentilesPerLotType() {
    var termsAggregationName = "lots-aggregation";

    var totalPriceField = Transaction.FIELD_PROPERTY_VALUE;
    var totalPriceAggregation = totalPriceField + "/percentiles";

    var m2PriceField = "price_m2";
    var m2PriceAggregation = m2PriceField + "/percentiles";

    var postalCodeQuery = QueryBuilders.wildcardQuery(Transaction.FIELD_POSTAL_CODE, "75*");
    var mutationNatureQuery = QueryBuilders.matchQuery(Transaction.FIELD_MUTATION_NATURE, "Vente");
    var localTypeQuery = QueryBuilders.matchQuery(Transaction.FIELD_LOCAL_TYPE, "Appartement");
    var query =
        QueryBuilders.boolQuery()
            .filter(postalCodeQuery)
            .filter(mutationNatureQuery)
            .filter(localTypeQuery);

    Map<String, Object> priceM2Mapping =
        Map.of(
            "type",
            "double",
            "script",
            "emit(doc['property_value'].value / doc['real_built_up_area'].value)");
    Map<String, Object> lotTypeMapping =
        Map.of(
            "type",
            "keyword",
            "script",
            "if (0 < doc['lots_count'].value && doc['lots_count'].value < 6) { emit('T' + doc['lots_count'].value) } else { emit('Others') }");
    Map<String, Object> runtimeMappings =
        Map.of("price_m2", priceM2Mapping, "lot_type", lotTypeMapping);

    var termsAggregation =
        AggregationBuilders.terms(termsAggregationName)
            .field("lot_type")
            .size(6) // T1, T2, T3, T4, T5, Others
            .subAggregation(
                AggregationBuilders.percentiles(totalPriceAggregation).field(totalPriceField))
            .subAggregation(
                AggregationBuilders.percentiles(m2PriceAggregation).field(m2PriceField));

    var sourceBuilder =
        new SearchSourceBuilder()
            .runtimeMappings(runtimeMappings)
            .aggregation(termsAggregation)
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = restClient.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + totalPriceField;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var terms = (ParsedStringTerms) response.getAggregations().get(termsAggregationName);
    return terms.getBuckets().stream()
        .collect(
            Collectors.toMap(
                MultiBucketsAggregation.Bucket::getKeyAsString,
                bucket -> toPercentilesArray(bucket.getAggregations()),
                (k1, k2) -> k1,
                TreeMap::new));
  }

  /**
   * Bucket aggregation, grouped by postal code.
   *
   * <p>Equivalent to HTTP request:
   *
   * <pre>
   * GET /transactions/_search
   *
   * {
   *     "query": {
   *         "match_all": {}
   *     },
   *     "size": 0,
   *     "aggs": {
   *         "postal_code/terms": {
   *             "terms": {
   *                 "field": "postal_code"
   *             }
   *         }
   *     }
   * }
   * </pre>
   *
   * @return a map of transaction counts (key: postal code, value: transaction count)
   */
  public Map<String, Long> mutationsByPostalCode() {
    var sourceBuilder =
        new SearchSourceBuilder()
            .size(0)
            .aggregation(
                AggregationBuilders.terms("postal_code/terms").field("postal_code").size(3))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = restClient.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation: " + sourceBuilder;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var terms = (ParsedStringTerms) response.getAggregations().get("postal_code/terms");
    return terms.getBuckets().stream()
        .map(b -> (ParsedBucket) b)
        .collect(Collectors.toMap(ParsedBucket::getKeyAsString, ParsedBucket::getDocCount));
  }
}
