package io.mincongh.elasticsearch;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests Search APIs.
 *
 * @author Mincong Huang
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/_search_apis.html">Search
 *     APIs | Java REST Client | Elastic</a>
 */
public class SearchTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    BulkResponse response =
        node()
            .client()
            .prepareBulk()
            .add(new IndexRequest("users").id("sansa").source("""
                {
                  "firstName": "Sansa",
                  "lastName": "Stark",
                  "gender": "female",
                  "house": "House Stark"
                }
                """, XContentType.JSON))
            .add(new IndexRequest("users").id("arya").source("""
                {
                  "firstName": "Arya",
                  "lastName": "Stark",
                  "gender": "female",
                  "house": "House Stark"
                }
                """, XContentType.JSON))
            .add(new IndexRequest("users").id("tyrion").source("""
                {
                  "firstName": "Tyrion",
                  "lastName": "Lannister",
                  "gender": "male",
                  "house": "House Lannister"
                }
                """, XContentType.JSON))
            .add(new IndexRequest("users").id("jaime").source("""
                {
                  "firstName": "Jaime",
                  "lastName": "Lannister",
                  "gender": "male",
                  "house": "House Lannister"
                }
                """, XContentType.JSON))
            .add(new IndexRequest("users").id("cersei").source("""
                {
                  "firstName": "Cersei",
                  "lastName": "Lannister",
                  "gender": "female",
                  "house": "House Lannister"
                }
                """, XContentType.JSON))
            .setRefreshPolicy(RefreshPolicy.WAIT_UNTIL)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, response.status());
    for (BulkItemResponse r : response.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  /**
   * Search API.
   *
   * @see <a
   *     href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html">Search
   *     API | Java REST Client | Elastic</a>
   */
  @Test
  public void searchApi_termQueryQuery() {
    SearchResponse response =
        node()
            .client()
            .prepareSearch("users")
            .setQuery(QueryBuilders.termQuery("lastName", "stark"))
            .get();

    SearchHits hits = response.getHits();
    assertEquals(2L, hits.getTotalHits().value);
    assertEquals("sansa", hits.getHits()[0].getId());
    assertEquals("arya", hits.getHits()[1].getId());
  }

  @Test
  public void searchApi_allMatchQuery() {
    SearchResponse response =
        node()
            .client()
            .prepareSearch("users")
            .setQuery(QueryBuilders.matchAllQuery())
            .get();

    SearchHits hits = response.getHits();
    assertEquals(5L, hits.getTotalHits().value);
  }
}
