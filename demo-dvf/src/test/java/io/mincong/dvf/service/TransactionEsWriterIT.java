package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

public class TransactionEsWriterIT extends ESRestTestCase {

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
  public void testCreateIndex() throws Exception {
    // Given
    var writer = new TransactionEsWriter(restClient, RefreshPolicy.IMMEDIATE);

    // When, Then
    Assertions.assertThatCode(writer::createIndex).doesNotThrowAnyException();
  }

  @Test
  public void testWrite() throws Exception {
    // Given
    var writer = new TransactionEsWriter(restClient, RefreshPolicy.IMMEDIATE);
    writer.createIndex();

    // When
    var ids = writer.write(Stream.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3)).get(10, SECONDS);

    // Then
    var objectMapper = Jackson.newObjectMapper();
    var request = new MultiGetRequest();
    ids.forEach(id -> request.add(Transaction.INDEX_NAME, id));
    var response = restClient.mget(request, RequestOptions.DEFAULT);
    Assertions.assertThat(response.getResponses())
        .extracting(MultiGetItemResponse::getResponse)
        .extracting(r -> objectMapper.readValue(r.getSourceAsString(), ImmutableTransaction.class))
        .containsExactlyInAnyOrder(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3);
  }
}
