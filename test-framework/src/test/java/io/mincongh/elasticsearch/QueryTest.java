package io.mincongh.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Test query APIs in Elasticsearch:
 *
 * <ul>
 *   <li>{@link GetRequest}
 *   <li>{@link MultiGetRequest}
 *   <li>{@link SearchRequest}.
 * </ul>
 *
 * @author Mincong Huang
 */
public class QueryTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    Map<String, String> sansa = new HashMap<>();
    sansa.put("firstName", "Sansa");
    sansa.put("lastName", "Stark");
    sansa.put("datetime", "2019-11-23");

    Map<String, String> arya = new HashMap<>();
    arya.put("firstName", "Arya");
    arya.put("lastName", "Stark");
    arya.put("datetime", "2019-11-23");

    BulkResponse response =
        node()
            .client()
            .prepareBulk()
            .add(new IndexRequest().index("users").id("sansa").source(sansa))
            .add(new IndexRequest().index("users").id("arya").source(arya))
            .setRefreshPolicy(RefreshPolicy.WAIT_UNTIL)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, response.status());
    for (BulkItemResponse r : response.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  @Test
  public void getRequest() {
    GetRequest request = new GetRequest().index("users").id("sansa");
    GetResponse response = node().client().get(request).actionGet();

    assertEquals("users", response.getIndex());
    assertEquals("sansa", response.getId());

    Map<String, Object> source = response.getSourceAsMap();
    assertEquals("Sansa", source.get("firstName"));
    assertEquals("Stark", source.get("lastName"));
    assertEquals("2019-11-23", source.get("datetime"));
  }

  @Test
  public void multiGetRequest() {
    MultiGetRequest request =
        new MultiGetRequest() //
            .add("users", "sansa")
            .add("users", "arya");
    MultiGetResponse response = node().client().multiGet(request).actionGet();
    MultiGetItemResponse[] responses = response.getResponses();

    assertEquals("users", responses[0].getIndex());
    assertEquals("sansa", responses[0].getId());
    Map<String, Object> source0 = responses[0].getResponse().getSourceAsMap();
    assertEquals("Sansa", source0.get("firstName"));
    assertEquals("Stark", source0.get("lastName"));
    assertEquals("2019-11-23", source0.get("datetime"));

    assertEquals("users", responses[1].getIndex());
    assertEquals("arya", responses[1].getId());
    Map<String, Object> source1 = responses[1].getResponse().getSourceAsMap();
    assertEquals("Arya", source1.get("firstName"));
    assertEquals("Stark", source1.get("lastName"));
    assertEquals("2019-11-23", source1.get("datetime"));
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
