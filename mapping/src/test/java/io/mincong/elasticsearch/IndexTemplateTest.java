package io.mincong.elasticsearch;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.test.ESSingleNodeTestCase;
import org.junit.Test;

public class IndexTemplateTest extends ESSingleNodeTestCase {

  @Test
  public void createTemplate() {
    // Given a user template to create

    // When creating it
    client()
        .admin()
        .indices()
        .preparePutTemplate("user_template")
        .setPatterns(List.of("user*"))
        .addMapping(
            "_doc",
            json("{'properties': {'name': {'type': 'keyword'}, 'age': {'type': 'long'}}}"),
            XContentType.JSON)
        .get();

    // Then the creation is successful
    var response = client().admin().indices().prepareGetTemplates("user_template").get();
    var metadata = response.getIndexTemplates().get(0);
    Assertions.assertThat(metadata.getName()).isEqualTo("user_template");
  }

  /**
   * This test demonstrates how index template is used when a new index is created and it matches
   * the expression of the index template.
   */
  @Test
  public void createIndex() {
    // Given an existing template
    client()
        .admin()
        .indices()
        .preparePutTemplate("user_template")
        .setPatterns(List.of("user*"))
        .addMapping(
            "_doc",
            json("{'properties': {'name': {'type': 'keyword'}, 'age': {'type': 'long'}}}"),
            XContentType.JSON)
        .get();

    // When creating an index matching this template
    var indexResponse =
        client()
            .prepareIndex()
            .setIndex("user_fr")
            .setSource(json("{'name': 'First Last', 'age': 30}"), XContentType.JSON)
            .execute()
            .actionGet();

    // Then the index is created
    Assertions.assertThat(indexResponse.status()).isEqualTo(RestStatus.CREATED);

    // And the mappings are correct
    var mappingResponse = client().admin().indices().prepareGetMappings("user_fr").get();
    @SuppressWarnings("unchecked")
    var properties =
        (Map<String, Map<String, ?>>)
            mappingResponse.mappings().get("user_fr").get("_doc").sourceAsMap().get("properties");
    Assertions.assertThat(properties)
        .hasSize(2)
        .containsEntry("age", Map.of("type", "long"))
        .containsEntry("name", Map.of("type", "keyword"));
  }

  String json(String singleQuoted) {
    return singleQuoted.replace("'", "\"");
  }
}
