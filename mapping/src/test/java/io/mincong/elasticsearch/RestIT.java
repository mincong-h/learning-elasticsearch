package io.mincong.elasticsearch;

import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

/**
 * @author Mincong Huang
 */
public class RestIT extends ESRestTestCase {

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("tests.rest.cluster", "localhost:9200");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.clearProperty("tests.rest.cluster");
  }

  @Test
  public void name() {
    System.out.println(System.getProperty("tests.rest.cluster"));
    System.out.println(client().getNodes());
  }
}
