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

  @JsonProperty("id_mutation")
  public abstract String mutationId();

  static {
    mappings.put("id_mutation", Map.of("type", "keyword"));
  }

  @JsonProperty("date_mutation")
  public abstract LocalDate mutationDate();

  static {
    mappings.put("date_mutation", Map.of("type", "date"));
  }

  @JsonProperty("numero_disposition")
  public abstract String dispositionNumber();

  @JsonProperty("nature_mutation")
  public abstract String mutationNature();

  @JsonProperty("valeur_fonciere")
  public abstract double propertyValue();

  @JsonProperty("adresse_numero")
  public abstract String addressNumber();

  @JsonProperty("adresse_suffixe")
  public abstract String addressSuffix();

  @JsonProperty("adresse_nom_voie")
  public abstract String addressRoadName();

  @JsonProperty("adresse_code_voie")
  public abstract String addressRoadCode();

  static {
    mappings.put("adresse_code_voie", Map.of("type", "keyword"));
  }

  @JsonProperty("code_postal")
  public abstract String postalCode();

  static {
    mappings.put("code_postal", Map.of("type", "keyword"));
  }

  @JsonProperty("code_commune")
  public abstract String communeCode();

  static {
    mappings.put("code_commune", Map.of("type", "keyword"));
  }

  @JsonProperty("nom_commune")
  public abstract String communeName();

  @JsonProperty("code_departement")
  public abstract String departmentCode();

  static {
    mappings.put("code_departement", Map.of("type", "keyword"));
  }

  @JsonProperty("ancien_code_commune")
  public abstract String oldCommuneCode();

  static {
    mappings.put("ancien_code_commune", Map.of("type", "keyword"));
  }

  @JsonProperty("ancien_nom_commune")
  public abstract String oldCommuneName();

  @JsonProperty("id_parcelle")
  public abstract String plotId();

  @JsonProperty("ancien_id_parcelle")
  public abstract String oldPlotId();

  @JsonProperty("numero_volume")
  public abstract String volumeNumber();

  @JsonProperty("lot1_numero")
  public abstract String numberLot1();

  @JsonProperty("lot1_surface_carrez")
  public abstract Optional<Double> surfaceSquareLot1();

  static {
    mappings.put("lot1_surface_carrez", Map.of("type", "double"));
  }

  @JsonProperty("lot2_numero")
  public abstract String numberLot2();

  @JsonProperty("lot2_surface_carrez")
  public abstract Optional<Double> surfaceSquareLot2();

  static {
    mappings.put("lot2_surface_carrez", Map.of("type", "double"));
  }

  @JsonProperty("lot3_numero")
  public abstract String numberLot3();

  @JsonProperty("lot3_surface_carrez")
  public abstract Optional<Double> surfaceSquareLot3();

  static {
    mappings.put("lot4_surface_carrez", Map.of("type", "double"));
  }

  @JsonProperty("lot4_numero")
  public abstract String numberLot4();

  @JsonProperty("lot4_surface_carrez")
  public abstract Optional<Double> surfaceSquareLot4();

  static {
    mappings.put("lot4_surface_carrez", Map.of("type", "double"));
  }

  @JsonProperty("lot5_numero")
  public abstract String numberLot5();

  @JsonProperty("lot5_surface_carrez")
  public abstract Optional<Double> surfaceSquareLot5();

  static {
    mappings.put("lot5_surface_carrez", Map.of("type", "double"));
  }

  @JsonProperty("nombre_lots")
  public abstract int lotsCount();

  static {
    mappings.put("nombre_lots", Map.of("type", "integer"));
  }

  @JsonProperty("code_type_local")
  public abstract String localTypeCode();

  static {
    mappings.put("code_type_local", Map.of("type", "keyword"));
  }

  @JsonProperty("type_local")
  public abstract String localType();

  @JsonProperty("surface_reelle_bati")
  public abstract Optional<Double> realBuiltUpArea();

  static {
    mappings.put("surface_reelle_bati", Map.of("type", "double"));
  }

  @JsonProperty("nombre_pieces_principales")
  public abstract Optional<Integer> principlePiecesCount();

  static {
    mappings.put("nombre_pieces_principales", Map.of("type", "integer"));
  }

  @JsonProperty("code_nature_culture")
  public abstract String natureCultureCode();

  @JsonProperty("nature_culture")
  public abstract String natureCulture();

  @JsonProperty("code_nature_culture_speciale")
  public abstract String specialNatureCultureCode();

  @JsonProperty("nature_culture_speciale")
  public abstract String specialNatureCulture();

  @JsonProperty("surface_terrain")
  public abstract double landSurface();

  static {
    mappings.put("surface_terrain", Map.of("type", "double"));
  }

  @JsonProperty("location")
  public abstract Optional<Location> location();

  static {
    mappings.put("location", Map.of("type", "geo_point"));
  }
}
