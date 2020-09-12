package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class IndexMapperParsingExceptionTest extends ESSingleNodeTestCase {

  @Test
  public void testMapperParsingException() {
    var request = new IndexRequest("my_index").source("{\"_id\": \"123\"}", XContentType.JSON);
    Assertions.assertThatThrownBy(() -> client().index(request).actionGet())
        .isInstanceOf(MapperParsingException.class)
        .hasMessageContaining(
            "Field [_id] is a metadata field and cannot be added inside a document. Use the index API request parameters");
  }
}
