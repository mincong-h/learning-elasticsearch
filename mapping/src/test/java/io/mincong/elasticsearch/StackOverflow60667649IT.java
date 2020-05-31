package io.mincong.elasticsearch;

import java.util.Map;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetFieldMappingsRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

/**
 * Put mapping in ElasticSearch by Java API
 *
 * <p>https://stackoverflow.com/questions/60667649
 *
 * @author Mincong Huang
 */
public class StackOverflow60667649IT extends ESRestTestCase {

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
  public void javaSource() throws Exception {
    XContentBuilder builder = XContentFactory.jsonBuilder();
    builder.startObject();
    builder.startObject("properties");
    builder.startObject("mje-test-location");
    builder.field("type", "geo_point");
    builder.endObject();
    builder.endObject();
    builder.endObject();

    var putMapping = new PutMappingRequest("my_index").source(builder);
    var putResponse = restClient.indices().putMapping(putMapping, RequestOptions.DEFAULT);
    Assertions.assertThat(putResponse.isAcknowledged()).isTrue();

    var getFieldMapping =
        new GetFieldMappingsRequest().indices("my_index").fields("mje-test-location");
    var mappingResponse =
        restClient.indices().getFieldMapping(getFieldMapping, RequestOptions.DEFAULT);

    @SuppressWarnings("unchecked")
    var field =
        (Map<String, Object>)
            mappingResponse
                .fieldMappings("my_index", "mje-test-location")
                .sourceAsMap()
                .get("mje-test-location");
    Assertions.assertThat(field).hasSize(1).containsEntry("type", "geo_point");
  }

  @Test
  public void stringSource() throws Exception {
    var putMapping =
        new PutMappingRequest("my_index")
            .source(
                "{\"properties\":{\"mje-test-location\":{\"type\":\"geo_point\"}}}",
                XContentType.JSON);
    var putResponse = restClient.indices().putMapping(putMapping, RequestOptions.DEFAULT);
    Assertions.assertThat(putResponse.isAcknowledged()).isTrue();

    var getFieldMapping =
        new GetFieldMappingsRequest().indices("my_index").fields("mje-test-location");
    var mappingResponse =
        restClient.indices().getFieldMapping(getFieldMapping, RequestOptions.DEFAULT);

    @SuppressWarnings("unchecked")
    var field =
        (Map<String, Object>)
            mappingResponse
                .fieldMappings("my_index", "mje-test-location")
                .sourceAsMap()
                .get("mje-test-location");
    Assertions.assertThat(field).hasSize(1).containsEntry("type", "geo_point");
  }
}
