package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class ClusterStatsLegacyClientTest extends ESSingleNodeTestCase {

  /**
   * @see <a
   *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-stats.html">Cluster
   *     stats API</a>
   */
  @Test
  public void getStats() {
    var client = client();

    // demo:start
    var clusterStatsResponse = client.admin().cluster().prepareClusterStats().get();
    // query value of `nodes.fs.available`
    var availableByteSize = clusterStatsResponse.getNodesStats().getFs().getAvailable();
    // demo:end

    Assertions.assertThat(availableByteSize.getBytes()).isPositive();
  }
}
