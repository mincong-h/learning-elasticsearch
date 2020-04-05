package io.mincong.elasticsearch;

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests "Update API".
 *
 * @author Mincong Huang
 */
public class UpdateTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    var response =
        client()
            .prepareIndex()
            .setIndex("users")
            .setId("1")
            .setSource("{\"name\":\"My Name\"}", XContentType.JSON)
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();
    assertEquals(RestStatus.CREATED, response.status());
    assertEquals(1L, response.getVersion());
  }

  @Test
  public void updateExistingDocument() {
    // Given an existing user "1"

    // When updating it
    var response =
        client() //
            .prepareUpdate()
            .setIndex("users")
            .setId("1")
            .setDoc("{\"age\":28}", XContentType.JSON)
            .setFetchSource(true) // fetch source for testing purpose
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    // Then the update is successful
    assertEquals(RestStatus.OK, response.status());
    assertEquals("users", response.getIndex());
    assertEquals("1", response.getId());

    // And the source is merged: both old and new keys exist
    var source = response.getGetResult().getSource();
    assertEquals("My Name", source.get("name"));
    assertEquals(28, source.get("age"));

    // And the version is incremented
    assertEquals(2L, response.getVersion());
  }

  @Test
  public void updateNonexistentDocument() {
    // Given a nonexistent user "2"

    // When updating it
    var response =
        client() //
            .prepareUpdate()
            .setIndex("users")
            .setId("2")
            .setDoc("{\"age\":28}", XContentType.JSON)
            .setDocAsUpsert(true) // use update-or-insert option to avoid exception
            .setFetchSource(true) // fetch source for testing purpose
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    // Then the update is successful
    assertEquals(RestStatus.CREATED, response.status());
    assertEquals("users", response.getIndex());
    assertEquals("2", response.getId());

    // And the source contains the original source
    var source = response.getGetResult().getSource();
    assertEquals(28, source.get("age"));

    // And this is the first version
    assertEquals(1L, response.getVersion());
  }

  @Test
  public void overwriteExistingField() {
    // Given an existing user "1"

    // When updating it
    var response =
        client() //
            .prepareUpdate()
            .setIndex("users")
            .setId("1")
            .setDoc("{\"name\":\"My New Name\",\"age\":28}", XContentType.JSON)
            .setDocAsUpsert(true) // use update-or-insert option to avoid exception
            .setFetchSource(true) // fetch source for testing purpose
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    // Then the update is successful
    assertEquals(RestStatus.OK, response.status());
    assertEquals("users", response.getIndex());
    assertEquals("1", response.getId());

    // And the source is updated: existing field is overwritten and new field is added
    var source = response.getGetResult().getSource();
    assertEquals(28, source.get("age"));
    assertEquals("My New Name", source.get("name"));

    // And the version is incremented
    assertEquals(2L, response.getVersion());
  }

  @Test
  public void dropExistingValue() {
    // Given an existing user "1"

    // When update it with name equal to null
    var response =
        client() //
            .prepareUpdate()
            .setIndex("users")
            .setId("1")
            .setDoc("{\"name\":null}", XContentType.JSON)
            .setFetchSource(true) // fetch source for testing purpose
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    // Then the update is successful and the existing value is overwritten
    var source = response.getGetResult().getSource();
    assertTrue(source.containsKey("name"));
    assertNull(source.get("name"));

    // And the version is incremented
    assertEquals(2L, response.getVersion());
  }
}
