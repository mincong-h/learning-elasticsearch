package io.mincong.dvf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Optional;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableTransaction.class)
@JsonDeserialize(as = ImmutableTransaction.class)
@JsonPropertyOrder({
  "id_mutation",
  "date_mutation",
  "numero_disposition",
  "nature_mutation",
  "valeur_fonciere",
  "adresse_numero",
  "adresse_suffixe",
  "adresse_nom_voie",
  "adresse_code_voie",
  "code_postal",
  "code_commune",
  "nom_commune",
  "code_departement",
  "ancien_code_commune",
  "ancien_nom_commune",
  "id_parcelle",
  "ancien_id_parcelle",
  "numero_volume",
  "lot1_numero",
  "lot1_surface_carrez",
  "lot2_numero",
  "lot2_surface_carrez",
  "lot3_numero",
  "lot3_surface_carrez",
  "lot4_numero",
  "lot4_surface_carrez",
  "lot5_numero",
  "lot5_surface_carrez",
  "nombre_lots",
  "code_type_local",
  "type_local",
  "surface_reelle_bati",
  "nombre_pieces_principales",
  "code_nature_culture",
  "nature_culture",
  "code_nature_culture_speciale",
  "nature_culture_speciale",
  "surface_terrain",
  "longitude",
  "latitude"
})
public interface Transaction {
  @JsonProperty("id_mutation")
  String mutationId();

  @JsonProperty("date_mutation")
  LocalDate mutationDate();

  @JsonProperty("numero_disposition")
  String dispositionNumber();

  @JsonProperty("nature_mutation")
  String mutationNature();

  @JsonProperty("valeur_fonciere")
  double propertyValue();

  @JsonProperty("adresse_numero")
  String addressNumber();

  @JsonProperty("adresse_suffixe")
  String addressSuffix();

  @JsonProperty("adresse_nom_voie")
  String addressRoadName();

  @JsonProperty("adresse_code_voie")
  String addressRoadCode();

  @JsonProperty("code_postal")
  String postalCode();

  @JsonProperty("code_commune")
  String communeCode();

  @JsonProperty("nom_commune")
  String communeName();

  @JsonProperty("code_departement")
  String departmentCode();

  @JsonProperty("ancien_code_commune")
  String oldCommuneCode();

  @JsonProperty("ancien_nom_commune")
  String oldCommuneName();

  @JsonProperty("id_parcelle")
  String plotId();

  @JsonProperty("ancien_id_parcelle")
  String oldPlotId();

  @JsonProperty("numero_volume")
  String volumeNumber();

  @JsonProperty("lot1_numero")
  String numberLot1();

  @JsonProperty("lot1_surface_carrez")
  double surfaceSquareLot1();

  @JsonProperty("lot2_numero")
  String numberLot2();

  @JsonProperty("lot2_surface_carrez")
  double surfaceSquareLot2();

  @JsonProperty("lot3_numero")
  String numberLot3();

  @JsonProperty("lot3_surface_carrez")
  double surfaceSquareLot3();

  @JsonProperty("lot4_numero")
  String numberLot4();

  @JsonProperty("lot4_surface_carrez")
  double surfaceSquareLot4();

  @JsonProperty("lot5_numero")
  String numberLot5();

  @JsonProperty("lot5_surface_carrez")
  double surfaceSquareLot5();

  @JsonProperty("nombre_lots")
  int numberOfLots();

  @JsonProperty("code_type_local")
  String localTypeCode();

  @JsonProperty("type_local")
  String localType();

  @JsonProperty("surface_reelle_bati")
  double realBuiltUpArea();

  @JsonProperty("nombre_pieces_principales")
  int principlePiecesCount();

  @JsonProperty("code_nature_culture")
  String natureCultureCode();

  @JsonProperty("nature_culture")
  String natureCulture();

  @JsonProperty("code_nature_culture_speciale")
  String specialNatureCultureCode();

  @JsonProperty("nature_culture_speciale")
  String specialNatureCulture();

  @JsonProperty("surface_terrain")
  String landSurface();

  @JsonProperty("longitude")
  Optional<Double> longitude();

  @JsonProperty("latitude")
  Optional<Double> latitude();
}
