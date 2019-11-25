package io.mincongh.elasticsearch;

import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.junit.Test;

/**
 * Test cluster.
 *
 * @author Mincong Huang
 */
@ClusterScope(numDataNodes = 1)
public class ClusterTest extends ESIntegTestCase {

  @Test
  public void numDataNodes() {
    assertEquals(1, cluster().numDataNodes());
  }
}
