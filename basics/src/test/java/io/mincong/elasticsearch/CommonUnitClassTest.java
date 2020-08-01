package io.mincong.elasticsearch;

import org.assertj.core.api.Assertions;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.junit.Test;

public class CommonUnitClassTest {

  @Test
  public void byteSizeValue() {
    var value = new ByteSizeValue(1, ByteSizeUnit.MB);

    // convert to bytes
    Assertions.assertThat(value.getBytes()).isEqualTo(1024 * 1024);

    // convert to KB
    Assertions.assertThat(value.getKb()).isEqualTo(1024);
    Assertions.assertThat(value.getKbFrac()).isEqualTo(1024.0); // preserve fraction

    // comparable
    var a = new ByteSizeValue(1, ByteSizeUnit.MB);
    var b = new ByteSizeValue(2, ByteSizeUnit.MB);
    Assertions.assertThat(a.compareTo(b)).isNegative();
    Assertions.assertThat(b.compareTo(a)).isPositive();
  }
}
