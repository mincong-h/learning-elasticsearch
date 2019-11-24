package io.mincongh.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
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
  public void indexing() {
    IndexRequest idxRequest =
        new IndexRequest() //
            .index("users")
            .id("sansa")
            .source(newSource());

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

  private Map<String, String> newSource() {
    Map<String, String> source = new HashMap<>();
    source.put("firstName", "Sansa");
    source.put("lastName", "Stark");
    return source;
  }
}
