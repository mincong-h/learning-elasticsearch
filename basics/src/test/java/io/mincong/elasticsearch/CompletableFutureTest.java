package io.mincong.elasticsearch;

import java.util.concurrent.CompletableFuture;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Wraps the client response future into {@link CompletableFuture}.
 *
 * @author Mincong Huang
 */
public class CompletableFutureTest extends ESSingleNodeTestCase {

  @Test
  public void getRequest() {
    var cf = new CompletableFuture<ClusterStateResponse>();
    client()
        .admin()
        .cluster()
        .prepareState()
        .execute(ActionListener.wrap(cf::complete, cf::completeExceptionally));

    var response = cf.join();
    Assertions.assertThat(response.getState().getNodes().getSize()).isEqualTo(1);
  }
}
