package io.mincongh.elasticsearch;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpIndexIT {

  @Test
  public void index() throws Exception {
    // HTTP Request
    URL url = new URL("http://localhost:9200/customer/_doc/sansa?pretty");
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
      assertThat(statusCode).isEqualTo(201);
      Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
      String content = s.hasNext() ? s.next() : "";
      System.out.println("PUT " + statusCode + " " + url);
      System.out.println(content);
      assertThatJson(content)
          .isObject()
          .containsEntry("_index", "customer")
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
      assertThatJson(content)
          .node("_shards")
          .isObject()
          .containsEntry("total", BigDecimal.valueOf(2))
          .containsEntry("successful", BigDecimal.valueOf(1))
          .containsEntry("failed", BigDecimal.valueOf(0));
    } finally {
      conn.disconnect();
    }
  }
}
