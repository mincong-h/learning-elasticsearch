package io.mincongh.elasticsearch;

import java.util.HashMap;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Tests "Index API".
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html">Index
 *     API | Java REST Client | Elastic</a>
 */
public class IndexTest extends ESSingleNodeTestCase {

  @Test
  public void itShouldIndexWithoutDocumentId() {
    IndexRequest idxRequest =
        new IndexRequest("msg").source("{\"msg\":\"Hello world!\"}", XContentType.JSON);
    IndexResponse idxResponse = client().index(idxRequest).actionGet();
    assertEquals("msg", idxResponse.getIndex());
    assertEquals(RestStatus.CREATED, idxResponse.status());
    logger.info("docId={}", idxRequest.id());
  }

  @Test
  public void itShouldIndexWithBuilder() {
    IndexResponse idxResponse =
        client()
            .prepareIndex()
            .setIndex("msg")
            .setSource("{\"msg\":\"Hello world!\"}", XContentType.JSON)
            .execute()
            .actionGet();
    assertEquals("msg", idxResponse.getIndex());
    assertEquals(RestStatus.CREATED, idxResponse.status());
  }

  @Test
  public void indexing() {
    HashMap<String, String> source = new HashMap<>();
    source.put("firstName", "Sansa");
    source.put("lastName", "Stark");

    IndexRequest idxRequest =
        new IndexRequest() //
            .index("users")
            .id("sansa")
            .source(source);

    IndexResponse idxResponse = node().client().index(idxRequest).actionGet();
    assertEquals("users", idxResponse.getIndex());
    assertEquals(RestStatus.CREATED, idxResponse.status());
    assertEquals("sansa", idxResponse.getId());
    assertEquals(1L, idxResponse.getPrimaryTerm());
    assertEquals(0L, idxResponse.getSeqNo());
    assertEquals(1L, idxResponse.getVersion());

    ShardInfo shardInfo = idxResponse.getShardInfo();
    assertEquals(0, shardInfo.getFailed());
    assertEquals(1, shardInfo.getSuccessful());
    assertEquals(1, shardInfo.getTotal());
  }
}
