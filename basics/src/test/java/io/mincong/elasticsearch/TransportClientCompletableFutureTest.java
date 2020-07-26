package io.mincong.elasticsearch;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Wraps the legacy client {@link org.elasticsearch.client.Client} response future into {@link
 * CompletableFuture}.
 *
 * @author Mincong Huang
 */
public class TransportClientCompletableFutureTest extends ESSingleNodeTestCase {

  @Test
  public void viaActionListenerWrap() {
    var client = client();

    // demo:start
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));
    var stateFuture = cf.thenApply(ClusterStateResponse::getState);
    // demo:end

    var clusterState = stateFuture.join();
    Assertions.assertThat(clusterState.getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void viaActionListener() {
    var client = client();

    // demo:start
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(
            new ActionListener<>() {

              @Override
              public void onResponse(ClusterStateResponse response) {
                cf.complete(response);
              }

              @Override
              public void onFailure(Exception e) {
                cf.completeExceptionally(e);
              }
            });
    // demo:end

    var response = cf.join();
    Assertions.assertThat(response.getState().getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void timeoutGet() throws Exception {
    var client = client();
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));

    // demo:start
    var response = cf.get(3000, TimeUnit.MILLISECONDS);
    // demo:end

    Assertions.assertThat(response.getState().getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void timeoutWithNumber() {
    var client = client();
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));

    // demo:start
    var responseFuture = cf.orTimeout(3000, TimeUnit.MILLISECONDS);
    // demo:end

    Assertions.assertThat(responseFuture.join().getState().getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void timeoutWithDuration() {
    var client = client();
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));

    // demo:start
    var timeout = Duration.ofSeconds(3);
    var responseFuture = cf.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
    // demo:end

    Assertions.assertThat(responseFuture.join().getState().getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void timeoutWithTimeValue() {
    var client = client();
    var cf = new CompletableFuture<ClusterStateResponse>();
    client
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));

    // demo:start
    var timeout = TimeValue.timeValueSeconds(3);
    var responseFuture = cf.orTimeout(timeout.millis(), TimeUnit.MILLISECONDS);
    // demo:end

    Assertions.assertThat(responseFuture.join().getState().getNodes().getSize()).isEqualTo(1);
  }

  @Test
  public void supplyAsyncWithExecutor() {
    var client = client();
    var executor = Executors.newSingleThreadExecutor();
    try {
      // demo:start
      var cf =
          CompletableFuture.supplyAsync(
              () -> client.admin().cluster().prepareState().get(), executor);
      // demo:end

      var response = cf.join();
      Assertions.assertThat(response.getState().getNodes().getSize()).isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
  }

  /**
   *
   *
   * <pre>
   * SEVERE: 1 thread leaked from SUITE scope at io.mincong.elasticsearch.TransportClientCompletableFutureTest:
   *    1) Thread[id=28, name=ForkJoinPool.commonPool-worker-3, state=WAITING, group=TGRP-TransportClientCompletableFutureTest]
   *         at java.base@14/jdk.internal.misc.Unsafe.park(Native Method)
   *         at java.base@14/java.util.concurrent.locks.LockSupport.park(LockSupport.java:211)
   *         at java.base@14/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1633)
   *         at java.base@14/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
   * </pre>
   */
  @Test
  @Ignore("thread leaked")
  public void supplyAsyncWithoutExecutor() {
    var client = client();
    // demo:start
    var cf = CompletableFuture.supplyAsync(() -> client.admin().cluster().prepareState().get());
    // demo:end

    var response = cf.join();
    Assertions.assertThat(response.getState().getNodes().getSize()).isEqualTo(1);
  }
}
