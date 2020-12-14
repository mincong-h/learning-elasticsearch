package io.mincong.dvf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

@Immutable
@Style(allParameters = true)
@JsonPropertyOrder({"longitude", "latitude"})
@JsonSerialize(as = ImmutableLocation.class)
@JsonDeserialize(as = ImmutableLocation.class)
public interface Location {

  @JsonProperty("longitude")
  Optional<Double> longitude();

  @JsonProperty("latitude")
  Optional<Double> latitude();
}
