package io.mincong.elasticsearch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

public class GetAsyncTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    Map<String, String> sansa = new HashMap<>();
    sansa.put("firstName", "Sansa");
    sansa.put("lastName", "Stark");

    Map<String, String> arya = new HashMap<>();
    arya.put("firstName", "Arya");
    arya.put("lastName", "Stark");

    BulkResponse response =
        client()
            .prepareBulk()
            .add(new IndexRequest().index("users").id("sansa").source(sansa))
            .add(new IndexRequest().index("users").id("arya").source(arya))
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();

    assertEquals(RestStatus.OK, response.status());
    for (BulkItemResponse r : response.getItems()) {
      assertEquals(RestStatus.CREATED, r.status());
    }
  }

  @Test
  public void getRequest_completableFuture() {
    var cf = new CompletableFuture<GetResponse>();
    client()
        .prepareGet()
        .setIndex("users")
        .setId("sansa")
        .execute(
            new ActionListener<>() {

              @Override
              public void onResponse(GetResponse response) {
                cf.complete(response);
              }

              @Override
              public void onFailure(Exception e) {
                cf.completeExceptionally(e);
              }
            });

    var response = cf.join();

    assertEquals("users", response.getIndex());
    assertEquals("sansa", response.getId());

    Map<String, Object> source = response.getSourceAsMap();
    assertEquals("Sansa", source.get("firstName"));
    assertEquals("Stark", source.get("lastName"));
  }

  @Test
  public void getRequest_customListener() {
    var listener = new EsListener<GetResponse>();
    client().prepareGet().setIndex("users").setId("sansa").execute(listener);

    var response = listener.getCompletableFuture().join();

    assertEquals("users", response.getIndex());
    assertEquals("sansa", response.getId());

    Map<String, Object> source = response.getSourceAsMap();
    assertEquals("Sansa", source.get("firstName"));
    assertEquals("Stark", source.get("lastName"));
  }

  static class EsListener<T> implements ActionListener<T> {

    private final CompletableFuture<T> completableFuture;

    public EsListener() {
      this.completableFuture = new CompletableFuture<>();
    }

    @Override
    public void onResponse(T response) {
      completableFuture.complete(response);
    }

    @Override
    public void onFailure(Exception e) {
      completableFuture.completeExceptionally(e);
    }

    public CompletableFuture<T> getCompletableFuture() {
      return completableFuture;
    }
  }
}
