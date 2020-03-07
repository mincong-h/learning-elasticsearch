package io.mincong.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests "Multi-Get API".
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-multi-get.html">Multi-Get
 *     API | Java REST Client | Elastic</a>
 */
public class MultiGetTest extends ESSingleNodeTestCase {

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

    BulkResponse response =
        client() //
            .prepareBulk()
            .add(new IndexRequest().index("users").id("sansa").source(sansa))
            .add(new IndexRequest().index("users").id("arya").source(arya))
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, response.status());
    for (BulkItemResponse r : response.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  @Test
  public void multiGetRequest() {
    MultiGetRequest request =
        new MultiGetRequest() //
            .add("users", "sansa")
            .add("users", "arya");
    MultiGetResponse response = client().multiGet(request).actionGet();
    MultiGetItemResponse[] responses = response.getResponses();

    assertEquals("users", responses[0].getIndex());
    assertEquals("sansa", responses[0].getId());
    Map<String, Object> source0 = responses[0].getResponse().getSourceAsMap();
    assertEquals("Sansa", source0.get("firstName"));
    assertEquals("Stark", source0.get("lastName"));

    assertEquals("users", responses[1].getIndex());
    assertEquals("arya", responses[1].getId());
    Map<String, Object> source1 = responses[1].getResponse().getSourceAsMap();
    assertEquals("Arya", source1.get("firstName"));
    assertEquals("Stark", source1.get("lastName"));
  }
}
