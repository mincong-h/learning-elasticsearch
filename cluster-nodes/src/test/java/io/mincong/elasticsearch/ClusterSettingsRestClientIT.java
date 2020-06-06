package io.mincong.elasticsearch;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

/**
 * Cluster get settings API
 *
 * @see ClusterSettingsLegacyClientTest
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-get-settings.html">
 *     Cluster get settings API</a>
 */
public class ClusterSettingsRestClientIT extends ESRestTestCase {

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

    var createRequest = new CreateIndexRequest("my_index");
    var response = restClient.indices().create(createRequest, RequestOptions.DEFAULT);
    Assertions.assertThat(response.isAcknowledged()).isTrue();
  }

  @After
  public void tearDown() throws Exception {
    restClient.close();
    super.tearDown();
  }

  @Test
  public void getSettings() throws IOException {
    var request = new ClusterGetSettingsRequest();

    // optional flags
    request.includeDefaults(true);
    request.local(true);

    var response = restClient.cluster().getSettings(request, RequestOptions.DEFAULT);

    // default settings
    Assertions.assertThat(response.getDefaultSettings().isEmpty()).isFalse();

    // persistent settings
    Assertions.assertThat(response.getPersistentSettings().isEmpty()).isTrue();

    // transient settings
    Assertions.assertThat(response.getTransientSettings().isEmpty()).isTrue();
  }
}
