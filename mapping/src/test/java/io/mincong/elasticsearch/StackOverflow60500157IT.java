package io.mincong.elasticsearch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

/**
 * Put mapping with Elastic Search's High level REST JAVA client asynchronously - deprecated error
 *
 * <p>https://stackoverflow.com/questions/60500157
 *
 * @author Mincong Huang
 */
public class StackOverflow60500157IT extends ESRestTestCase {

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("tests.rest.cluster", "localhost:19200");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.clearProperty("tests.rest.cluster");
  }

  private RestHighLevelClient client;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    var builder = RestClient.builder(new HttpHost("localhost", 19200, "http"));
    client = new RestHighLevelClient(builder);

    var createRequest = new CreateIndexRequest("contacts");
    var response = client.indices().create(createRequest, RequestOptions.DEFAULT);
    Assertions.assertThat(response.isAcknowledged()).isTrue();
  }

  @After
  public void tearDown() throws Exception {
    client.close();
    super.tearDown();
  }

  @Test
  public void oldRequest() {
    var source =
        "{\"properties\":{\"list_id\":{\"type\":\"integer\"},\"contact_id\":{\"type\":\"integer\"}}}";
    var request =
        new org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest("contacts")
            .source(source, XContentType.JSON);
    Assertions.assertThatExceptionOfType(ActionRequestValidationException.class)
        .isThrownBy(
            () -> {
              @SuppressWarnings("deprecation")
              var response = client.indices().putMapping(request, RequestOptions.DEFAULT);
            })
        .withMessageContaining("mapping type is missing");
  }

  @Test
  public void newRequest() throws Exception {
    var source =
        "{\"properties\":{\"list_id\":{\"type\":\"integer\"},\"contact_id\":{\"type\":\"integer\"}}}";
    var request =
        new org.elasticsearch.client.indices.PutMappingRequest("contacts")
            .source(source, XContentType.JSON);

    var response = client.indices().putMapping(request, RequestOptions.DEFAULT);
    Assertions.assertThat(response.isAcknowledged()).isTrue();
  }

  @Test
  public void newRequestAsync() throws Exception {
    var latch = new CountDownLatch(1);
    var source =
        "{\"properties\":{\"list_id\":{\"type\":\"integer\"},\"contact_id\":{\"type\":\"integer\"}}}";
    var request =
        new org.elasticsearch.client.indices.PutMappingRequest("contacts")
            .source(source, XContentType.JSON);

    var response = new AtomicReference<AcknowledgedResponse>();
    client
        .indices()
        .putMappingAsync(
            request,
            RequestOptions.DEFAULT,
            new ActionListener<>() {
              @Override
              public void onResponse(AcknowledgedResponse r) {
                response.set(r);
                latch.countDown();
              }

              @Override
              public void onFailure(Exception e) {
                latch.countDown();
              }
            });
    latch.await(3, TimeUnit.SECONDS);
    Assertions.assertThat(response.get().isAcknowledged()).isTrue();
  }
}
