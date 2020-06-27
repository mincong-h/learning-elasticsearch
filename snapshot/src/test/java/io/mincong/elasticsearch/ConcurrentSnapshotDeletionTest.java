package io.mincong.elasticsearch;

import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequestBuilder;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.metadata.RepositoryMetadata;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Concurrent snapshot deletion in Elasticsearch.
 *
 * @author Mincong Huang
 */
public class ConcurrentSnapshotDeletionTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    insertDocs();
    createRepo();
    createSnapshots();
  }

  private void insertDocs() {
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

  public void createRepo() {
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
        .extracting(RepositoryMetadata::name)
        .containsExactly("snapshotRepository");
  }

  private void createSnapshots() throws Exception {
    var requestU =
        new CreateSnapshotRequestBuilder(client(), CreateSnapshotAction.INSTANCE)
            .setIndices("users")
            .setSnapshot("users-snapshot")
            .setRepository("snapshotRepository")
            .setWaitForCompletion(true)
            .request();

    var requestC =
        new CreateSnapshotRequestBuilder(client(), CreateSnapshotAction.INSTANCE)
            .setIndices("companies")
            .setSnapshot("companies-snapshot")
            .setRepository("snapshotRepository")
            .setWaitForCompletion(true)
            .request();

    var responseU = client().admin().cluster().createSnapshot(requestU).get();
    var infoU = responseU.getSnapshotInfo();
    Assertions.assertThat(infoU.failedShards()).isZero();
    Assertions.assertThat(infoU.successfulShards()).isGreaterThan(0);
    Assertions.assertThat(infoU.indices()).containsExactly("users");
    Assertions.assertThat(infoU.status()).isEqualTo(RestStatus.OK);

    var responseC = client().admin().cluster().createSnapshot(requestC).get();
    var infoC = responseC.getSnapshotInfo();
    Assertions.assertThat(infoC.failedShards()).isZero();
    Assertions.assertThat(infoC.successfulShards()).isGreaterThan(0);
    Assertions.assertThat(infoC.indices()).containsExactly("companies");
    Assertions.assertThat(infoC.status()).isEqualTo(RestStatus.OK);
  }

  @Test
  public void removeSnapshot() throws ExecutionException, InterruptedException {
    var requestU =
        new DeleteSnapshotRequestBuilder(client(), DeleteSnapshotAction.INSTANCE)
            .setSnapshots("users-snapshot")
            .setRepository("snapshotRepository")
            .request();
    var requestC =
        new DeleteSnapshotRequestBuilder(client(), DeleteSnapshotAction.INSTANCE)
            .setSnapshots("companies-snapshot")
            .setRepository("snapshotRepository")
            .request();

    var responseU = client().admin().cluster().deleteSnapshot(requestU).get();
    var responseC = client().admin().cluster().deleteSnapshot(requestC).get();
    Assertions.assertThat(responseU.isAcknowledged()).isTrue();
    Assertions.assertThat(responseC.isAcknowledged()).isTrue();
  }
}
