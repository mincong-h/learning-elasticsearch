package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

public abstract class TransactionEsWriterAbstractIT extends ESRestTestCase {

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("tests.rest.cluster", "localhost:19200");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.clearProperty("tests.rest.cluster");
  }

  protected RestHighLevelClient restClient;

  protected abstract EsWriter newEsWriter();

  protected final int year = 2020;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    var builder = RestClient.builder(new HttpHost("localhost", 19200, "http"));
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
    var writer = newEsWriter();

    // When, Then
    Assertions.assertThatCode(writer::createIndex).doesNotThrowAnyException();
  }

  @Test
  public void testWrite() throws Exception {
    // Given
    var writer = newEsWriter();
    writer.createIndex();

    // When
    var transactions = new ImmutableTransaction[] {TRANSACTION_1, TRANSACTION_2, TRANSACTION_3};
    var count = writer.write(transactions).get(10, SECONDS);

    // Then
    Assertions.assertThat(count).isEqualTo(3L);

    var objectMapper = Jackson.newObjectMapper();
    var request = new SearchRequest().indices(Transaction.indexNameForYear(year));
    var response = restClient.search(request, RequestOptions.DEFAULT);
    Assertions.assertThat(response.getHits().getHits())
        .extracting(
            hit -> objectMapper.readValue(hit.getSourceAsString(), ImmutableTransaction.class))
        .containsExactlyInAnyOrder(transactions);
  }
}
