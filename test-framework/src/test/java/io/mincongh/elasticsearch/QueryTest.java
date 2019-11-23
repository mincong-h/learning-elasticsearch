package io.mincongh.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Test query APIs in Elasticsearch: {@link GetRequest}, {@link MultiGetRequest}.
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

    IndexResponse r1 =
        node()
            .client()
            .index(new IndexRequest().index("users").id("sansa").source(sansa))
            .actionGet();
    IndexResponse r2 =
        node()
            .client()
            .index(new IndexRequest().index("users").id("arya").source(arya))
            .actionGet();

    assertEquals(RestStatus.CREATED, r1.status());
    assertEquals(RestStatus.CREATED, r2.status());
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
}
