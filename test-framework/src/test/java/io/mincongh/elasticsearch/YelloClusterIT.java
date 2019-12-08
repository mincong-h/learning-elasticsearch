package io.mincongh.elasticsearch;

import java.util.Map;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.junit.Test;

/**
 * Elasticsearch cluster is {@link ClusterHealthStatus#YELLOW} when the primary shard is allocated
 * but replicas are not. Here, we have only one data node but we need at least two data nodes: one
 * for primary shard and one for replica shard. Since the condition is not satisfied, the cluster
 * health status is YELLOW.
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-health.html">Cluster
 *     Health API | Elasticsearch Reference</a>
 */
@ClusterScope(numDataNodes = 1)
public class YelloClusterIT extends ESIntegTestCase {

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
    ClusterHealthStatus status = ensureYellowAndNoInitializingShards("users");
    assertEquals(ClusterHealthStatus.YELLOW, status);
  }
}
