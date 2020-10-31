package io.mincong.elasticsearch.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Test;

/**
 * @author Mincong Huang
 * @blog https://mincong.io/2020/10/25/java-time/
 */
public class BlogJavaTimeTimeValueTest {

  @Test
  public void toDuration() {
    TimeValue timeValue = TimeValue.timeValueMinutes(5);
    Duration duration = Duration.ofMillis(timeValue.millis());
    assertThat(duration).isEqualTo(Duration.ofMinutes(5));
  }

  @Test
  public void toTimeValue() {
    Duration duration = Duration.ofMinutes(5);
    TimeValue timeValue = TimeValue.timeValueMillis(duration.toMillis());
    assertThat(timeValue).isEqualTo(TimeValue.timeValueMinutes(5));
  }

  @Test
  public void fromSettings() {
    Settings settings = Settings.builder().put("timeout", "5m").build();
    TimeValue timeout = settings.getAsTime("timeout", TimeValue.ZERO);
    assertThat(timeout).isEqualTo(TimeValue.timeValueMinutes(5));
  }
}
