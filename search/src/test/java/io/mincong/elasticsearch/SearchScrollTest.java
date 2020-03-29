package io.mincong.elasticsearch;

import java.util.ArrayList;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Elasticsearch Search Scroll API
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-scroll">Elasticsearch
 *     REST API: Request Body Search - Scroll</a>
 */
public class SearchScrollTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    var bulkRequest = client().prepareBulk();
    for (int i = 0; i < 300; i++) {
      bulkRequest.add(new IndexRequest("my_index").id(String.valueOf(i)).source(Map.of()));
    }
    bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE).execute().actionGet();
  }

  @Test
  public void scroll() {
    var results = new ArrayList<String>();

    // first request
    var searchResponse =
        client()
            .prepareSearch()
            .setIndices("my_index")
            .setSize(100)
            .setScroll(TimeValue.timeValueMinutes(1))
            .execute()
            .actionGet();
    for (var hit : searchResponse.getHits()) {
      results.add(hit.getId());
    }
    logger.info("results={} ({} new), scrollId={}",
          results.size(),
          results.size(),
          searchResponse.getScrollId());

    // more requests
    var scrollId = searchResponse.getScrollId();
    var hasNext = !results.isEmpty();
    while (hasNext) {
      var resp =
          client()
              .prepareSearchScroll(scrollId)
              .setScroll(TimeValue.timeValueMinutes(1))
              .execute()
              .actionGet();
      var newResults = new ArrayList<String>();
      for (var hit : resp.getHits()) {
        newResults.add(hit.getId());
      }
      results.addAll(newResults);
      logger.info("results={} ({} new), scrollId={}",
          results.size(),
          newResults.size(),
          resp.getScrollId());

      hasNext = !newResults.isEmpty();
      scrollId = resp.getScrollId();
    }

    assertEquals(300, results.size());
  }
}
