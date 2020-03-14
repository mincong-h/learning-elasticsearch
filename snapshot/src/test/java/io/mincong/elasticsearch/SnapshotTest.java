package io.mincong.elasticsearch;

import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequestBuilder;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse.IndexResult;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Snapshot and restore in Elasticsearch.
 *
 * @author Mincong Huang
 */
public class SnapshotTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    client()
        .prepareIndex()
        .setIndex("users")
        .setId("user1")
        .setSource("{\"name\":\"Tom\"}", XContentType.JSON)
        .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
        .execute();

    client()
        .prepareIndex()
        .setIndex("companies")
        .setId("elastic")
        .setSource("{\"name\":\"Elastic\"}", XContentType.JSON)
        .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
        .execute();
  }

  @Override
  @After
  public void tearDown() throws Exception {
    removeSnapshot();
    super.tearDown();
  }

  @Test
  public void createSnapshot() throws Exception {
    // create repository "snapshotRepository" before creating snapshot
    var repoStr = client().settings().get("path.repo");
    var settings =
        Settings.builder() //
            .put("location", repoStr)
            .put("compress", true)
            .build();
    var acknowledgedResponse =
        client()
            .admin()
            .cluster()
            .preparePutRepository("snapshotRepository")
            .setType("fs")
            .setSettings(settings)
            .setVerify(true)
            .get();
    Assertions.assertThat(acknowledgedResponse.isAcknowledged()).isTrue();
    var repositories =
        client()
            .admin()
            .cluster()
            .prepareGetRepositories("snapshotRepository")
            .get()
            .repositories();
    Assertions.assertThat(repositories)
        .extracting(RepositoryMetaData::name)
        .containsExactly("snapshotRepository");

    // create snapshot using "snapshotRepository"
    var createSnapshotRequest =
        new CreateSnapshotRequestBuilder(client(), CreateSnapshotAction.INSTANCE)
            .setIndices("users", "companies")
            .setSnapshot("snapshot1")
            .setRepository("snapshotRepository")
            .setWaitForCompletion(true)
            .request();

    var createSnapshotResponse =
        client().admin().cluster().createSnapshot(createSnapshotRequest).get();
    var snapshotInfo = createSnapshotResponse.getSnapshotInfo();
    Assertions.assertThat(snapshotInfo.failedShards()).isZero();
    Assertions.assertThat(snapshotInfo.successfulShards()).isGreaterThan(0);
    Assertions.assertThat(snapshotInfo.indices()).containsExactlyInAnyOrder("users", "companies");
    Assertions.assertThat(snapshotInfo.status()).isEqualTo(RestStatus.OK);
  }

  @Test
  public void restoreSnapshot() throws Exception {
    createSnapshot();

    /*
     * The restore operation can be performed on a functioning cluster.
     * However, an existing index can be only restored if it’s closed
     * and has the same number of shards as the index in the snapshot.
     */
    var closeIndexResponse = client().admin().indices().prepareClose("users", "companies").get();
    Assertions.assertThat(closeIndexResponse.getIndices())
        .extracting(IndexResult::getIndex)
        .extracting(Index::getName)
        .containsExactlyInAnyOrder("users", "companies");

    // restore snapshot using "snapshotRepository"
    var restoreSnapshotRequest =
        new RestoreSnapshotRequestBuilder(client(), RestoreSnapshotAction.INSTANCE)
            .setIndices("users", "companies")
            .setSnapshot("snapshot1")
            .setRepository("snapshotRepository")
            .setWaitForCompletion(true)
            .request();

    var restoreSnapshotResponse =
        client().admin().cluster().restoreSnapshot(restoreSnapshotRequest).get();
    var restoreInfo = restoreSnapshotResponse.getRestoreInfo();
    Assertions.assertThat(restoreInfo.failedShards()).isZero();
    Assertions.assertThat(restoreInfo.successfulShards()).isGreaterThan(0);
    Assertions.assertThat(restoreInfo.indices()).containsExactlyInAnyOrder("users", "companies");
    Assertions.assertThat(restoreInfo.status()).isEqualTo(RestStatus.OK);
  }

  private void removeSnapshot() throws ExecutionException, InterruptedException {
    var deleteSnapshot =
        new DeleteSnapshotRequestBuilder(client(), DeleteSnapshotAction.INSTANCE)
            .setSnapshot("snapshot1")
            .setRepository("snapshotRepository")
            .request();

    var acknowledgeResponse = client().admin().cluster().deleteSnapshot(deleteSnapshot).get();
    Assertions.assertThat(acknowledgeResponse.isAcknowledged()).isTrue();
  }
}
