package io.mincong.elasticsearch.index;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class IndexNotFoundExceptionTest extends ESSingleNodeTestCase {

  @Override
  protected Settings nodeSettings() {
    return Settings.builder()
        /*
         * Disable auto index creation
         *
         * See https://www.elastic.co/guide/en/elasticsearch/reference/7.9/docs-index_.html#index-creation
         */
        .put("action.auto_create_index", false)
        .build();
  }

  @Test
  public void testAutoCreationDisabled() {
    var request = new IndexRequest("my_index").source("{\"msg\": \"hello\"}", XContentType.JSON);

    Assertions.assertThatThrownBy(() -> client().index(request).actionGet())
        .isInstanceOf(IndexNotFoundException.class)
        .hasMessageContaining("no such index [my_index]");
  }
}
