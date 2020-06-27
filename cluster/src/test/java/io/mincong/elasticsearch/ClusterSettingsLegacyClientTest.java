package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Cluster get settings API
 *
 * @see ClusterSettingsRestClientIT
 */
public class ClusterSettingsLegacyClientTest extends ESSingleNodeTestCase {

  @Test
  public void getSettings() {
    var clusterStateResponse = client().admin().cluster().prepareState().get();
    var metaData = clusterStateResponse.getState().metadata();

    Assertions.assertThat(metaData.transientSettings().isEmpty()).isTrue();
    Assertions.assertThat(metaData.persistentSettings().isEmpty()).isTrue();
    Assertions.assertThat(metaData.settings().isEmpty()).isTrue();
  }
}
