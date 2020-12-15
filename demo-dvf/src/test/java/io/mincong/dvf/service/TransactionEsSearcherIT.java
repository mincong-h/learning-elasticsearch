package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Map;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

public class TransactionEsSearcherIT extends ESRestTestCase {

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

    var writer = new TransactionEsWriter(restClient, RefreshPolicy.IMMEDIATE);
    writer.createIndex();
    writer
        .write(Stream.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4))
        .get(10, SECONDS)
        .forEach(id -> logger.info("Transaction " + id));
  }

  @After
  public void tearDown() throws Exception {
    restClient.close();
    super.tearDown();
  }

  @Test
  public void testSumAggregation() {
    // Given
    var searcher = new TransactionEsSearcher(restClient);

    // When
    var sum = searcher.sumAggregate("valeur_fonciere");

    // Then
    Assertions.assertThat(sum.getValue())
        .isEqualTo(261_000.0)
        .isEqualTo(
            TRANSACTION_1.propertyValue()
                + TRANSACTION_2.propertyValue()
                + TRANSACTION_3.propertyValue()
                + TRANSACTION_4.propertyValue());
  }

  @Test
  public void testPostalCode() {
    // Given
    var searcher = new TransactionEsSearcher(restClient);

    // When
    var stats = searcher.transactionByPostalCode(QueryBuilders.matchAllQuery());

    // Then
    Assertions.assertThat(stats).isEqualTo(Map.of("01340", 2L, "01250", 1L, "01960", 1L));
  }
}
