package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.elasticsearch.test.ESIntegTestCase.Scope;
import org.junit.Test;

/**
 * Tests "Nodes Info API".
 *
 * @author Mincong Huang
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-info.html">Nodes
 *     Info API | Elastic</a>
 */
@ClusterScope(minNumDataNodes = 3, maxNumDataNodes = 3, scope = Scope.TEST)
public class ClusterNodesInfoIT extends ESIntegTestCase {

  @Test
  public void getRequest() {
    var nodesInfoResponse = client().admin().cluster().prepareNodesInfo().all().get();

    var dataNodeCount = 0;

    for (var nodeInfo : nodesInfoResponse.getNodes()) {
      if (DiscoveryNode.isDataNode(nodeInfo.getSettings())) {
        dataNodeCount++;
      }
    }

    Assertions.assertThat(dataNodeCount).isEqualTo(3);
  }
}
