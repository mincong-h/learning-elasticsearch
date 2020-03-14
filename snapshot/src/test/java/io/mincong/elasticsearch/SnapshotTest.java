package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotAction;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
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
    var request =
        new CreateSnapshotRequestBuilder(client(), CreateSnapshotAction.INSTANCE)
            .setIndices("users", "companies")
            .setSnapshot("snapshot1")
            .setRepository("snapshotRepository")
            .setWaitForCompletion(true)
            .request();

    var response = client().admin().cluster().createSnapshot(request).get();
    var snapshotInfo = response.getSnapshotInfo();
    Assertions.assertThat(snapshotInfo.failedShards()).isZero();
    Assertions.assertThat(snapshotInfo.successfulShards()).isGreaterThan(0);
    Assertions.assertThat(snapshotInfo.indices()).containsExactlyInAnyOrder("users", "companies");
    Assertions.assertThat(snapshotInfo.status()).isEqualTo(RestStatus.OK);
  }
}
