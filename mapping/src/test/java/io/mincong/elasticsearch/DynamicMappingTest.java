package io.mincong.elasticsearch;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Test dynamic mapping in Elasticsearch.
 *
 * <p>Fields and mapping types do not need to be defined before being used. Thanks to dynamic
 * mapping, new field names will be added automatically, just by indexing a document. New fields can
 * be added both to the top-level mapping type, and to inner object and nested fields.
 *
 * @author Mincong Huang
 */
public class DynamicMappingTest extends ESSingleNodeTestCase {

  @Test
  public void typeText() {
    index("{\"aText\": \"Hello world\"}");

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("aText"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse.fieldMappings("my_index", "_doc", "aText").sourceAsMap().get("aText");
    var fields = Map.of("keyword", Map.of("type", "keyword", "ignore_above", 256));
    Assertions.assertThat(messageField)
        .hasSize(2)
        .containsEntry("type", "text")
        .containsEntry("fields", fields);
  }

  @Test
  public void typeDate() {
    index("{\"aDate\": \"2020-04-04T16:00:00\"}");

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("aDate"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse.fieldMappings("my_index", "_doc", "aDate").sourceAsMap().get("aDate");
    Assertions.assertThat(messageField).hasSize(1).containsEntry("type", "date");
  }

  @Test
  public void typeLong() {
    index("{\"aLong\": 123}");

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("aLong"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse.fieldMappings("my_index", "_doc", "aLong").sourceAsMap().get("aLong");
    Assertions.assertThat(messageField).hasSize(1).containsEntry("type", "long");
  }

  @Test
  public void typeDouble() {
    index("{\"aFloat\": 123.4}");

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("aFloat"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse.fieldMappings("my_index", "_doc", "aFloat").sourceAsMap().get("aFloat");
    Assertions.assertThat(messageField).hasSize(1).containsEntry("type", "float");
  }

  @Test
  public void typeBoolean() {
    index("{\"aBoolean\": true}");

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("aBoolean"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse
                .fieldMappings("my_index", "_doc", "aBoolean")
                .sourceAsMap()
                .get("aBoolean");
    Assertions.assertThat(messageField).hasSize(1).containsEntry("type", "boolean");
  }

  private void index(String source) {
    var indexResponse =
        client()
            .prepareIndex()
            .setIndex("my_index")
            .setSource(source, XContentType.JSON)
            .setRefreshPolicy(RefreshPolicy.IMMEDIATE)
            .execute()
            .actionGet();
    Assertions.assertThat(indexResponse.status()).isEqualTo(RestStatus.CREATED);
  }
}
