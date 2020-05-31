package io.mincong.elasticsearch;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.http.HttpHost;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Assert;
import org.junit.Test;

public class HttpIndexIT {

  @Test
  public void index() throws Exception {
    // HTTP Request
    URL url = new URL("http://localhost:9200/users/_doc/sansa?pretty");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("PUT");
    conn.setRequestProperty("Content-Type", "application/json");
    try (OutputStream os = conn.getOutputStream();
        Writer writer = new OutputStreamWriter(os, UTF_8)) {
      // language=json
      writer.write("{ \"name\": \"Sansa Stark\" }");
    }

    // HTTP Response
    try {
      conn.connect();
      int statusCode = conn.getResponseCode();
      Assertions.assertThat(statusCode).isEqualTo(201);
      Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
      String content = s.hasNext() ? s.next() : "";
      System.out.println("PUT " + statusCode + " " + url);
      System.out.println(content);
      JsonAssertions.assertThatJson(content)
          .isObject()
          .containsEntry("_index", "users")
          .containsEntry("_type", "_doc")
          .containsEntry("_id", "sansa")
          /*
           * The first time a document is uploaded, the version is set to 1.
           * Then, the following modifications will result to version
           * incrementation: 2, 3, 4, ...
           */
          .containsKey("_version")
          .containsEntry("result", "created")
          .containsKey("_seq_no")
          .containsEntry("_primary_term", BigDecimal.valueOf(1));
      JsonAssertions.assertThatJson(content)
          .node("_shards")
          .isObject()
          .containsEntry("total", BigDecimal.valueOf(2))
          .containsEntry("successful", BigDecimal.valueOf(1))
          .containsEntry("failed", BigDecimal.valueOf(0));
    } finally {
      conn.disconnect();
    }
  }

  @Test
  public void itShouldIndexWithRestClient() throws Exception {
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    var idxRequest =
        new IndexRequest("my_index").source("{\"msg\":\"Hello world!\"}", XContentType.JSON);
    try (var client = new RestHighLevelClient(builder)) {
      var idxResponse = client.index(idxRequest, RequestOptions.DEFAULT);
      Assert.assertEquals("my_index", idxResponse.getIndex());
      Assert.assertEquals(RestStatus.CREATED, idxResponse.status());
    }
  }
}
