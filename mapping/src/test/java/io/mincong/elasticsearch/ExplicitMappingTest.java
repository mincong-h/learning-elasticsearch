package io.mincong.elasticsearch;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

/**
 * Test explicit mapping in Elasticsearch.
 *
 * <p>You know more about your data than Elasticsearch can guess, so while dynamic mapping can be
 * useful to get started, at some point you will want to specify your own explicit mappings.
 *
 * @author Mincong Huang
 */
public class ExplicitMappingTest extends ESSingleNodeTestCase {

  @Test
  public void createIndexWithExplictMapping() {
    var create =
        new CreateIndexRequest("my_index")
            .mapping(
                "_doc", "{\"properties\":{\"message\":{\"type\":\"text\"}}}", XContentType.JSON);
    var response = client().admin().indices().create(create).actionGet();
    Assertions.assertThat(response.isAcknowledged()).isTrue();

    var mappingResponse =
        client()
            .admin()
            .indices()
            .getFieldMappings(new GetFieldMappingsRequest().indices("my_index").fields("message"))
            .actionGet();
    @SuppressWarnings("unchecked")
    var messageField =
        (Map<String, Object>)
            mappingResponse
                .fieldMappings("my_index", "_doc", "message")
                .sourceAsMap()
                .get("message");
    Assertions.assertThat(messageField).hasSize(1).containsEntry("type", "text");
  }
}
