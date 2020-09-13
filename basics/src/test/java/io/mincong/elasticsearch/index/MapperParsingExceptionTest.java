package io.mincong.elasticsearch.index;

import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class MapperParsingExceptionTest extends ESSingleNodeTestCase {

  @Test
  public void testMetadataField() {
    var request = new IndexRequest("my_index").source("{\"_id\": \"123\"}", XContentType.JSON);
    Assertions.assertThatThrownBy(() -> client().index(request).actionGet())
        .isInstanceOf(MapperParsingException.class)
        .hasMessageContaining(
            "Field [_id] is a metadata field and cannot be added inside a document. Use the index API request parameters");
  }

  @Test
  public void testWrongMapping() {
    var request1 =
        new IndexRequest("my_index2")
            .source("{\"updated\": \"2020-09-12T21:12:00\"}", XContentType.JSON);
    var response1 = client().index(request1).actionGet();
    Assertions.assertThat(response1.status()).isEqualTo(RestStatus.CREATED);

    var request2 =
        new IndexRequest("my_index2")
            .source(
                "{\"updated\": {\"date\": \"2020-09-12\", \"time\": \"21:12:00\"}}",
                XContentType.JSON);
    /*
     * failed to parse field [updated] of type [date] in document with id
     * 'vZLKg3QBwzbK8KxrfutG'. Preview of field's value:
     * '{date=2020-09-12, time=21:12:00}'
     */
    Assertions.assertThatThrownBy(() -> client().index(request2).actionGet())
        .isInstanceOf(MapperParsingException.class)
        .hasMessageStartingWith(
            "failed to parse field [updated] of type [date] in document with id")
        .hasMessageEndingWith("Preview of field's value: '{date=2020-09-12, time=21:12:00}'");
  }
}
