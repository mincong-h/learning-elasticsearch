package io.mincong.elasticsearch;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests boolean query via Search API.
 *
 * <p>Comment: https://mincong.io/2019/11/24/essinglenodetestcase/#comment-4852306721
 *
 * @author Mincong Huang
 */
public class BlogDisqus4852306721Test extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    var bulkResponse =
        client()
            .prepareBulk()
            .add(
                new IndexRequest("transactions")
                    .id("account1.tx1")
                    .source(
                        Map.of(
                            "transactionDate", "2020-03-19T00:00:00",
                            "accountId", "1",
                            "amount", -10.0),
                        XContentType.JSON))
            .add(
                new IndexRequest("transactions")
                    .id("account1.tx2")
                    .source(
                        Map.of(
                            "transactionDate", "2020-03-20T00:00:00",
                            "accountId", "1",
                            "amount", -20.0),
                        XContentType.JSON))
            .add(
                new IndexRequest("transactions")
                    .id("account2.tx3")
                    .source(
                        Map.of(
                            "transactionDate", "2020-03-21T00:00:00",
                            "accountId", "2",
                            "amount", -30.0),
                        XContentType.JSON))
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, bulkResponse.status());
    for (var r : bulkResponse.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  @Test
  public void booleanQuery() {
    var sourceBuilder =
        QueryBuilders.boolQuery()
            .must(QueryBuilders.rangeQuery("transactionDate").gte("2020-03-20").lte("2020-03-28"))
            .must(QueryBuilders.matchQuery("accountId", "1"));

    var request = new SearchRequest().source(new SearchSourceBuilder().query(sourceBuilder));
    var response = client().search(request).actionGet();

    Assertions.assertThat(response.getHits().getHits())
        .hasSize(1)
        .extracting(SearchHit::getId)
        .containsExactly("account1.tx2");
  }
}
