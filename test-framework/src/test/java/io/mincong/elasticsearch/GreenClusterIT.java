package io.mincong.elasticsearch;

import java.util.Map;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.junit.Test;

/**
 * Elasticsearch cluster is {@link ClusterHealthStatus#GREEN} when all shards are allocated (both
 * primary and replicas). Here, we have two data nodes: one for primary shard and one for replica
 * shard. Since the condition is satisfied, the cluster health status is GREEN.
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-health.html">Cluster
 *     Health API | Elasticsearch Reference</a>
 */
@ClusterScope(numDataNodes = 2)
public class GreenClusterIT extends ESIntegTestCase {

  @Override
  public int minimumNumberOfReplicas() {
    return 1;
  }

  @Override
  public int maximumNumberOfReplicas() {
    return 1;
  }

  @Test
  public void yellowClusterWhenMissingReplicas() {
    client()
        .prepareIndex()
        .setIndex("users")
        .setSource(Map.of("foo", "bar"))
        .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
        .execute()
        .actionGet();
    ClusterHealthStatus status = ensureGreen("users");
    assertEquals(ClusterHealthStatus.GREEN, status);
  }
}
