package io.mincong.dvf.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Test;

public class JacksonTest {

  @Test
  public void newObjectMapper() throws Exception {
    var mapper = Jackson.newObjectMapper();
    var string = mapper.writeValueAsString(LocalDate.of(2020, 12, 16));
    assertThat(string).isEqualTo("\"2020-12-16\"");
  }
}
