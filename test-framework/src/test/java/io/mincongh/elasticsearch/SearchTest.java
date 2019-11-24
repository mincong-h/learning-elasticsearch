package io.mincongh.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests Search API.
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html">Search
 *     API | Java REST Client | Elastic</a>
 */
public class SearchTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    Map<String, String> sansa = new HashMap<>();
    sansa.put("firstName", "Sansa");
    sansa.put("lastName", "Stark");

    Map<String, String> arya = new HashMap<>();
    arya.put("firstName", "Arya");
    arya.put("lastName", "Stark");

    Map<String, String> tyrion = new HashMap<>();
    tyrion.put("firstName", "Tyrion");
    tyrion.put("lastName", "Lannister");

    BulkResponse response =
        node()
            .client()
            .prepareBulk()
            .add(new IndexRequest().index("users").id("sansa").source(sansa))
            .add(new IndexRequest().index("users").id("arya").source(arya))
            .add(new IndexRequest().index("users").id("tyrion").source(tyrion))
            .setRefreshPolicy(RefreshPolicy.WAIT_UNTIL)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, response.status());
    for (BulkItemResponse r : response.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  @Test
  public void searchRequest() {
    SearchResponse response =
        node()
            .client()
            .prepareSearch("users")
            .setQuery(QueryBuilders.termQuery("lastName", "stark"))
            .get();

    SearchHits hits = response.getHits();
    assertEquals(2L, hits.getTotalHits().value);
    assertEquals("sansa", hits.getHits()[0].getId());
    assertEquals("arya", hits.getHits()[1].getId());
  }
}
