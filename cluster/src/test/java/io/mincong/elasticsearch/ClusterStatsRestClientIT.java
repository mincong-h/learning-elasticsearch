package io.mincong.elasticsearch;

import org.apache.http.HttpHost;
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
  public void getStats() {
    // TODO(mhuang): how to fetch this?
    //  restClient.cluster() does not provide a method for stats
  }
}
