package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.cluster.RestoreInProgress;
import org.elasticsearch.cluster.SnapshotDeletionsInProgress;
import org.elasticsearch.cluster.SnapshotsInProgress;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Retrieves snapshot operations progress via cluster state: snapshots-in-progress,
 * restore-in-progress, snapshot-deletions-in-progress.
 *
 * <p>These tests are not real tests, they are written for demo purpose.
 *
 * @author Mincong Huang
 */
public class SnapshotStateDemoTest extends ESSingleNodeTestCase {

  @Test
  public void methodPrepareState() {
    ClusterStateResponse r =
        client()
            .admin() //
            .cluster()
            .prepareState()
            .clear()
            .setCustoms(true)
            .get();

    RestoreInProgress restore = r.getState().custom(RestoreInProgress.TYPE);
    SnapshotsInProgress snapshots = r.getState().custom(SnapshotsInProgress.TYPE);
    SnapshotDeletionsInProgress deletions = r.getState().custom(SnapshotDeletionsInProgress.TYPE);

    Assertions.assertThat(restore).isNull();
    Assertions.assertThat(snapshots).isNull();
    Assertions.assertThat(deletions).isNull();
  }

  @Test
  public void methodClusterStateRequest() {
    ClusterStateRequest request = new ClusterStateRequest().clear().customs(true);
    ClusterStateResponse r =
        client()
            .admin() //
            .cluster()
            .state(request)
            .actionGet();

    RestoreInProgress restore = r.getState().custom(RestoreInProgress.TYPE);
    SnapshotsInProgress snapshots = r.getState().custom(SnapshotsInProgress.TYPE);
    SnapshotDeletionsInProgress deletions = r.getState().custom(SnapshotDeletionsInProgress.TYPE);

    Assertions.assertThat(restore).isNull();
    Assertions.assertThat(snapshots).isNull();
    Assertions.assertThat(deletions).isNull();
  }
}
