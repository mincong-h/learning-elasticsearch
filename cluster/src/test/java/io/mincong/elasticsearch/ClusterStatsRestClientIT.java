package io.mincong.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.assertj.core.api.Assertions;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

public class ClusterStatsRestClientIT extends ESRestTestCase {

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("tests.rest.cluster", "localhost:9200");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.clearProperty("tests.rest.cluster");
  }

  private RestHighLevelClient restClient;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    restClient = new RestHighLevelClient(builder);
  }

  @After
  public void tearDown() throws Exception {
    restClient.close();
    super.tearDown();
  }

  @Test
  public void getStats() throws IOException {
    // demo:start
    var request = new Request("GET", "/_nodes/_all/stats/fs");
    var response = restClient.getLowLevelClient().performRequest(request);
    var body = EntityUtils.toString(response.getEntity());
    var node = new ObjectMapper().readValue(body, ObjectNode.class);
    var firstNodeMetrics = node.get("nodes").fields().next().getValue();
    var bytes = firstNodeMetrics.get("fs").get("total").get("available_in_bytes").asLong();
    // demo:end

    Assertions.assertThat(bytes).isPositive();
  }
}
