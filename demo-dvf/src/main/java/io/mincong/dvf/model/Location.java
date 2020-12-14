package io.mincong.dvf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * @see <a
 *     href="https://www.elastic.co/guide/en/elasticsearch/reference/7.9/geo-point.html">Geo-point
 *     field type</a>
 */
@Immutable
@JsonSerialize(as = ImmutableLocation.class)
@JsonDeserialize(as = ImmutableLocation.class)
public interface Location {

  static Location of(double longitude, double latitude) {
    return ImmutableLocation.builder().longitude(longitude).latitude(latitude).build();
  }

  @JsonProperty("lon") // Name "lon" is required by Elasticsearch
  double longitude();

  @JsonProperty("lat") // Name "lat" is required by Elasticsearch
  double latitude();
}
