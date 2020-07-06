package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Before;
import org.junit.Test;

public class IndexStatsTest extends ESSingleNodeTestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    IndexRequest idxRequest =
        new IndexRequest("msg").source("{\"msg\":\"Hello world!\"}", XContentType.JSON);
    IndexResponse idxResponse = client().index(idxRequest).actionGet();
    assertEquals("msg", idxResponse.getIndex());
    assertEquals(RestStatus.CREATED, idxResponse.status());
  }

  @Test
  public void itShouldReturnEmptyStats() {
    var response = client().admin().indices().prepareStats().clear().get();
    Assertions.assertThat(response.getIndices()).containsOnlyKeys("msg");
    var index = response.getIndex("msg");
    // all stats are null, some examples:
    Assertions.assertThat(index.getPrimaries().completion).isNull();
    Assertions.assertThat(index.getPrimaries().docs).isNull();
    Assertions.assertThat(index.getPrimaries().fieldData).isNull();
  }
}
