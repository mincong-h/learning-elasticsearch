package io.mincongh.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests "Get API".
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-get.html">Get
 *     API | Java REST Client | Elastic</a>
 */
public class GetTest extends ESSingleNodeTestCase {

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
        client()
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
    GetResponse response =
        client() //
            .prepareGet()
            .setIndex("users")
            .setId("sansa")
            .execute()
            .actionGet();

    assertEquals("users", response.getIndex());
    assertEquals("sansa", response.getId());

    Map<String, Object> source = response.getSourceAsMap();
    assertEquals("Sansa", source.get("firstName"));
    assertEquals("Stark", source.get("lastName"));
  }
}
