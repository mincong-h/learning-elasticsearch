package io.mincongh.elasticsearch;

import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.test.ESIntegTestCase.ClusterScope;
import org.junit.Test;

/**
 * Test cluster.
 *
 * @author Mincong Huang
 */
@ClusterScope(numDataNodes = 3)
public class ClusterTest extends ESIntegTestCase {

  @Test
  public void numDataNodes() {
    assertEquals(3, cluster().numDataNodes());
  }
}
