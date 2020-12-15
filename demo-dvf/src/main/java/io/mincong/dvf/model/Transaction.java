package io.mincong.dvf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableTransaction.class)
@JsonDeserialize(as = ImmutableTransaction.class)
public abstract class Transaction {

  public static final String INDEX_NAME = "transactions";

  /** See https://www.elastic.co/guide/en/elasticsearch/reference/7.9/mapping-types.html */
  private static final Map<String, Object> mappings = new HashMap<>();

  public static Map<String, Object> esMappings() {
    return Map.of("properties", Map.copyOf(mappings));
  }

  @JsonProperty("mutation_id")
  public abstract String mutationId();

  static {
    mappings.put("mutation_id", Map.of("type", "keyword"));
  }

  @JsonProperty("mutation_date")
  public abstract LocalDate mutationDate();

  static {
    mappings.put("mutation_date", Map.of("type", "date"));
  }

  @JsonProperty("disposition_number")
  public abstract String dispositionNumber();

  @JsonProperty("mutation_nature")
  public abstract String mutationNature();

  @JsonProperty("property_value")
  public abstract double propertyValue();

  @JsonProperty("address_number")
  public abstract String addressNumber();

  @JsonProperty("address_suffix")
  public abstract String addressSuffix();

  @JsonProperty("address_road_name")
  public abstract String addressRoadName();

  @JsonProperty("address_road_code")
  public abstract String addressRoadCode();

  static {
    mappings.put("address_road_code", Map.of("type", "keyword"));
  }

  @JsonProperty("postal_code")
  public abstract String postalCode();

  static {
    mappings.put("postal_code", Map.of("type", "keyword"));
  }

  @JsonProperty("commune_code")
  public abstract String communeCode();

  static {
    mappings.put("commune_code", Map.of("type", "keyword"));
  }

  @JsonProperty("commune_name")
  public abstract String communeName();

  @JsonProperty("department_code")
  public abstract String departmentCode();

  static {
    mappings.put("department_code", Map.of("type", "keyword"));
  }

  @JsonProperty("old_commune_code")
  public abstract String oldCommuneCode();

  static {
    mappings.put("old_commune_code", Map.of("type", "keyword"));
  }

  @JsonProperty("old_commune_name")
  public abstract String oldCommuneName();

  @JsonProperty("plot_id")
  public abstract String plotId();

  static {
    mappings.put("plot_id", Map.of("type", "keyword"));
  }

  @JsonProperty("old_plot_id")
  public abstract String oldPlotId();

  static {
    mappings.put("old_plot_id", Map.of("type", "keyword"));
  }

  @JsonProperty("volume_number")
  public abstract String volumeNumber();

  @JsonProperty("number_lot1")
  public abstract String numberLot1();

  @JsonProperty("surface_lot1")
  public abstract Optional<Double> surfaceSquareLot1();

  static {
    mappings.put("surface_lot1", Map.of("type", "double"));
  }

  @JsonProperty("number_lot2")
  public abstract String numberLot2();

  @JsonProperty("surface_lot2")
  public abstract Optional<Double> surfaceSquareLot2();

  static {
    mappings.put("surface_log2", Map.of("type", "double"));
  }

  @JsonProperty("number_lot3")
  public abstract String numberLot3();

  @JsonProperty("surface_lot3")
  public abstract Optional<Double> surfaceSquareLot3();

  static {
    mappings.put("surface_lot3", Map.of("type", "double"));
  }

  @JsonProperty("number_lot4")
  public abstract String numberLot4();

  @JsonProperty("surface_lot4")
  public abstract Optional<Double> surfaceSquareLot4();

  static {
    mappings.put("surface_lot4", Map.of("type", "double"));
  }

  @JsonProperty("number_lot5")
  public abstract String numberLot5();

  @JsonProperty("surface_lot5")
  public abstract Optional<Double> surfaceSquareLot5();

  static {
    mappings.put("surface_lot5", Map.of("type", "double"));
  }

  @JsonProperty("lots_count")
  public abstract int lotsCount();

  static {
    mappings.put("lots_count", Map.of("type", "integer"));
  }

  @JsonProperty("local_code_type")
  public abstract String localTypeCode();

  static {
    mappings.put("local_code_type", Map.of("type", "keyword"));
  }

  @JsonProperty("local_type")
  public abstract String localType();

  @JsonProperty("real_built_up_area")
  public abstract Optional<Double> realBuiltUpArea();

  static {
    mappings.put("real_built_up_area", Map.of("type", "double"));
  }

  @JsonProperty("principle_pieces_count")
  public abstract Optional<Integer> principlePiecesCount();

  static {
    mappings.put("principle_pieces_count", Map.of("type", "integer"));
  }

  @JsonProperty("nature_culture_code")
  public abstract String natureCultureCode();

  @JsonProperty("nature_culture")
  public abstract String natureCulture();

  @JsonProperty("special_nature_culture_code")
  public abstract String specialNatureCultureCode();

  static {
    mappings.put("special_nature_culture_code", Map.of("type", "keyword"));
  }

  @JsonProperty("special_nature_culture")
  public abstract String specialNatureCulture();

  @JsonProperty("land_surface")
  public abstract double landSurface();

  static {
    mappings.put("land_surface", Map.of("type", "double"));
  }

  @JsonProperty("location")
  public abstract Optional<Location> location();

  static {
    mappings.put("location", Map.of("type", "geo_point"));
  }
}
