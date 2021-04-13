package io.mincong.elasticsearch;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.test.rest.ESRestTestCase;
import org.junit.*;

/**
 * Wraps the response of Java High Level REST Client {@link RestHighLevelClient} into {@link
 * CompletableFuture}.
 *
 * @author Mincong Huang
 * @blog https://mincong.io/2020/07/26/es-client-completablefuture/
 */
public class CompletableFutureRestClientIT extends ESRestTestCase {

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("tests.rest.cluster", "localhost:19200");
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.clearProperty("tests.rest.cluster");
  }

  private RestHighLevelClient restClient;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    var builder = RestClient.builder(new HttpHost("localhost", 19200, "http"));
    restClient = new RestHighLevelClient(builder);
  }

  @After
  public void tearDown() throws Exception {
    restClient.close();
    super.tearDown();
  }

  @Test
  public void viaActionListenerWrap() {
    // demo:start
    var cf = new CompletableFuture<ClusterHealthResponse>();
    restClient
        .cluster()
        .healthAsync(
            new ClusterHealthRequest(),
            RequestOptions.DEFAULT,
            ActionListener.wrap(cf::complete, cf::completeExceptionally));
    // demo:end

    var response = cf.join();
    Assertions.assertThat(response.getNumberOfNodes()).isEqualTo(1);
  }

  @Test
  public void viaActionListener() {
    // demo:start
    var cf = new CompletableFuture<ClusterHealthResponse>();
    restClient
        .cluster()
        .healthAsync(
            new ClusterHealthRequest(),
            RequestOptions.DEFAULT,
            new ActionListener<>() {

              @Override
              public void onResponse(ClusterHealthResponse response) {
                cf.complete(response);
              }

              @Override
              public void onFailure(Exception e) {
                cf.completeExceptionally(e);
              }
            });
    // demo:end

    var response = cf.join();
    Assertions.assertThat(response.getNumberOfNodes()).isEqualTo(1);
  }

  @Test
  public void supplyAsyncWithExecutor() {
    var executor = Executors.newSingleThreadExecutor();
    try {
      // demo:start
      var cf =
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  return restClient
                      .cluster()
                      .health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
                } catch (IOException e) {
                  throw new CompletionException(e);
                }
              },
              executor);
      // demo:end

      var response = cf.join();
      Assertions.assertThat(response.getNumberOfNodes()).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
  }

  /**
   *
   *
   * <pre>
   * SEVERE: 1 thread leaked from SUITE scope at io.mincong.elasticsearch.RestClientCompletableFutureIT:
   *    1) Thread[id=32, name=ForkJoinPool.commonPool-worker-3, state=WAITING, group=TGRP-RestClientCompletableFutureIT]
   *         at java.base@14/jdk.internal.misc.Unsafe.park(Native Method)
   *         at java.base@14/java.util.concurrent.locks.LockSupport.park(LockSupport.java:211)
   *         at java.base@14/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1633)
   *         at java.base@14/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
   * </pre>
   */
  @Test
  @Ignore("thread leaked")
  public void supplyAsyncWithoutExecutor() {
    // demo:start
    var cf =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                return restClient
                    .cluster()
                    .health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
              } catch (IOException e) {
                throw new CompletionException(e);
              }
            });
    // demo:end

    var response = cf.join();
    Assertions.assertThat(response.getNumberOfNodes()).isEqualTo(1);
  }
}
