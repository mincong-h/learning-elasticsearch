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
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;
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
  private final RestHighLevelClient client;

  public TransactionEsAggregator(RestHighLevelClient client) {
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
   *         "mutation_id/count": {
   *             "count": {
   *                 "field": "mutation_id"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public ValueCount mutationIdsCount() {
    var fieldName = Transaction.FIELD_MUTATION_ID;
    var aggregationName = fieldName + "/count";
    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.count(aggregationName).field(fieldName))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    try {
      var searchResponse = client.search(request, RequestOptions.DEFAULT);
      return (ValueCount) searchResponse.getAggregations().asMap().get(aggregationName);
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
   *         "match_all": {}
   *     },
   *     "aggs": {
   *         "property_value/min": {
   *             "min": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/avg": {
   *             "avg": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/max": {
   *             "max": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/sum": {
   *             "sum": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/count": {
   *             "count": {
   *                 "field": "property_value"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public PropertyValueStats aggregations() {
    var fieldName = Transaction.FIELD_PROPERTY_VALUE;
    var minAggregationName = fieldName + "/min";
    var sumAggregationName = fieldName + "/avg";
    var maxAggregationName = fieldName + "/max";
    var avgAggregationName = fieldName + "/sum";
    var countAggregationName = fieldName + "/count";
    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.sum(sumAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.avg(avgAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.min(minAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.max(maxAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.count(countAggregationName).field(fieldName))
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
    var results = response.getAggregations().asMap();
    return new PropertyValueStats(
        ((Min) results.get(minAggregationName)).getValue(),
        ((Avg) results.get(avgAggregationName)).getValue(),
        ((Max) results.get(maxAggregationName)).getValue(),
        ((Sum) results.get(sumAggregationName)).getValue(),
        ((ValueCount) results.get(countAggregationName)).getValue());
  }

  /**
   * Equivalent to HTTP request:
   *
   * <pre>
   * {
   *     "query": {
   *         "match_all": {}
   *     },
   *     "runtime_mappings": {
   *         "price_m2": {
   *             "type": "double",
   *             "script": "emit(doc['property_value'].value * doc['real_built_up_area'].value)"
   *         }
   *     },
   *     "aggs": {
   *         "price_m2/stats": {
   *             "stats": {
   *                 "field": "price_m2"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  // TODO rename PropertyValueStats
  public PropertyValueStats priceStats() {
    var fieldName = "price_m2";
    var statsAggregationName = fieldName + "/stats";

    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.stats(statsAggregationName).field(fieldName))
            .query(QueryBuilders.matchAllQuery());

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    try {
      var response = client.search(request, RequestOptions.DEFAULT);
      return new PropertyValueStats(response.getAggregations().get(statsAggregationName));
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
   *         "wildcard": {
   *             "postal_code": {
   *                 "value": "75*",
   *                 "boost": 1.0,
   *                 "rewrite": "constant_score"
   *             }
   *         },
   *         "match": {
   *             "mutation_nature": {
   *                 "query": "Vente"
   *             }
   *         },
   *         "match": {
   *             "local_type": {
   *                 "query": "Appartement"
   *             }
   *         }
   *     },
   *     "aggs": {
   *         "property_value/min": {
   *             "min": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/avg": {
   *             "avg": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/max": {
   *             "max": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/sum": {
   *             "sum": {
   *                 "field": "property_value"
   *             }
   *         },
   *         "property_value/count": {
   *             "count": {
   *                 "field": "property_value"
   *             }
   *         }
   *     }
   * }
   * </pre>
   */
  public PropertyValueStats parisStatsOverview() {
    var fieldName = Transaction.FIELD_PROPERTY_VALUE;
    var minAggregationName = fieldName + "/min";
    var sumAggregationName = fieldName + "/avg";
    var maxAggregationName = fieldName + "/max";
    var avgAggregationName = fieldName + "/sum";
    var countAggregationName = fieldName + "/count";

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
            .aggregation(AggregationBuilders.sum(sumAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.avg(avgAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.min(minAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.max(maxAggregationName).field(fieldName))
            .aggregation(AggregationBuilders.count(countAggregationName).field(fieldName))
            .query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var results = response.getAggregations().asMap();
    return new PropertyValueStats(
        ((Min) results.get(minAggregationName)).getValue(),
        ((Avg) results.get(avgAggregationName)).getValue(),
        ((Max) results.get(maxAggregationName)).getValue(),
        ((Sum) results.get(sumAggregationName)).getValue(),
        ((ValueCount) results.get(countAggregationName)).getValue());
  }

  /**
   * This is a sub-aggregation. See
   * https://www.elastic.co/guide/en/elasticsearch/reference/7.x/search-aggregations.html
   *
   * <p>Equivalent to HTTP request:
   *
   * <pre>
   * {
   *     "query": {
   *         "wildcard": {
   *             "postal_code": {
   *                 "value": "75*",
   *                 "boost": 1.0,
   *                 "rewrite": "constant_score"
   *             }
   *         },
   *         "match": {
   *             "mutation_nature": {
   *                 "query": "Vente"
   *             }
   *         },
   *         "match": {
   *             "local_type": {
   *                 "query": "Appartement"
   *             }
   *         }
   *     },
   *     "aggs": {
   *         "postal-code-aggregation": {
   *             "terms": {
   *                 "field": "property_value"
   *             },
   *             "aggs": {
   *                 "property_value/stats": {
   *                     "stats": {
   *                         "field": "property_value"
   *                     }
   *                 }
   *             }
   *         }
   *     }
   * }
   * </pre>
   *
   * @return a map of stats where the key is the postal code and the value is its related
   *     statistics.
   */
  public SortedMap<String, PropertyValueStats> parisStatsPerPostalCode() {
    var termsAggregationName = "postal-code-aggregation";

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

    var termsAggregation =
        AggregationBuilders.terms(termsAggregationName)
            .field(Transaction.FIELD_POSTAL_CODE)
            .size(20)
            .subAggregation(AggregationBuilders.stats(statsAggregationName).field(fieldName));

    var sourceBuilder = new SearchSourceBuilder().aggregation(termsAggregation).query(query);

    var request = new SearchRequest().indices(Transaction.INDEX_NAME).source(sourceBuilder);

    SearchResponse response;
    try {
      response = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      var msg = "Failed to search for aggregation of field: " + fieldName;
      logger.error(msg, e);
      throw new IllegalStateException(msg, e);
    }
    var terms = (ParsedStringTerms) response.getAggregations().asMap().get(termsAggregationName);
    return terms.getBuckets().stream()
        .collect(
            Collectors.toMap(
                MultiBucketsAggregation.Bucket::getKeyAsString,
                bucket ->
                    new PropertyValueStats(bucket.getAggregations().get(statsAggregationName)),
                (k1, k2) -> k1,
                TreeMap::new));
  }

  public Map<String, Long> mutationsByPostalCode() {
    var sourceBuilder =
        new SearchSourceBuilder()
            .aggregation(AggregationBuilders.terms("postal_code_agg").field("postal_code"))
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

  public static class PropertyValueStats {

    public final double min;
    public final double avg;
    public final double max;
    public final double sum;
    public final long count;

    private PropertyValueStats(Stats stats) {
      this.min = stats.getMin();
      this.avg = stats.getAvg();
      this.max = stats.getMax();
      this.sum = stats.getSum();
      this.count = stats.getCount();
    }

    private PropertyValueStats(double min, double avg, double max, double sum, long count) {
      this.min = min;
      this.avg = avg;
      this.max = max;
      this.sum = sum;
      this.count = count;
    }
  }
}
