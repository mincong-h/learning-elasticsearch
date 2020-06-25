package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.cluster.block.ClusterBlocks;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class ClusterStateTest extends ESSingleNodeTestCase {

  @Test
  public void getMinimumClusterState() {
    var clusterState =
        client() //
            .admin()
            .cluster()
            .prepareState()
            .clear()
            /*
             * Define your options explicitly here as:
             *
             *     setXxx(...)
             */
            .setCustoms(true)
            .get()
            .getState();

    Assertions.assertThat(clusterState.blocks()).isEqualTo(ClusterBlocks.EMPTY_CLUSTER_BLOCK);
    Assertions.assertThat(clusterState.metaData().indices()).isEmpty();
    Assertions.assertThat(clusterState.routingTable().allShards()).isEmpty();
    Assertions.assertThat(clusterState.nodes()).isEmpty();
  }
}
