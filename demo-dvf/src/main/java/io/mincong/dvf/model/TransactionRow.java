package io.mincong.dvf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.Optional;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonSerialize(as = ImmutableTransactionRow.class)
@JsonDeserialize(as = ImmutableTransactionRow.class)
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
  "location",
  "longitude",
  "latitude"
})
public interface TransactionRow {

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
  Optional<Double> surfaceSquareLot1();

  @JsonProperty("lot2_numero")
  String numberLot2();

  @JsonProperty("lot2_surface_carrez")
  Optional<Double> surfaceSquareLot2();

  @JsonProperty("lot3_numero")
  String numberLot3();

  @JsonProperty("lot3_surface_carrez")
  Optional<Double> surfaceSquareLot3();

  @JsonProperty("lot4_numero")
  String numberLot4();

  @JsonProperty("lot4_surface_carrez")
  Optional<Double> surfaceSquareLot4();

  @JsonProperty("lot5_numero")
  String numberLot5();

  @JsonProperty("lot5_surface_carrez")
  Optional<Double> surfaceSquareLot5();

  @JsonProperty("nombre_lots")
  int lotsCount();

  @JsonProperty("code_type_local")
  String localTypeCode();

  @JsonProperty("type_local")
  String localType();

  @JsonProperty("surface_reelle_bati")
  Optional<Double> realBuiltUpArea();

  @JsonProperty("nombre_pieces_principales")
  Optional<Integer> principlePiecesCount();

  @JsonProperty("code_nature_culture")
  String natureCultureCode();

  @JsonProperty("nature_culture")
  String natureCulture();

  @JsonProperty("code_nature_culture_speciale")
  String specialNatureCultureCode();

  @JsonProperty("nature_culture_speciale")
  String specialNatureCulture();

  @JsonProperty("surface_terrain")
  double landSurface();

  @JsonProperty("longitude")
  Optional<Double> longitude();

  @JsonProperty("latitude")
  Optional<Double> latitude();

  default ImmutableTransaction toTransactionObj() {
    final Optional<Location> optLocation;
    if (longitude().isPresent() && latitude().isPresent()) {
      optLocation = Optional.of(Location.of(longitude().get(), latitude().get()));
    } else {
      optLocation = Optional.empty();
    }

    return ImmutableTransaction.builder()
        .mutationId(mutationId())
        .mutationDate(mutationDate())
        .dispositionNumber(dispositionNumber())
        .mutationNature(mutationNature())
        .propertyValue(propertyValue())
        .addressNumber(addressNumber())
        .addressSuffix(addressSuffix())
        .addressRoadName(addressRoadName())
        .addressRoadCode(addressRoadCode())
        .postalCode(postalCode())
        .communeCode(communeCode())
        .communeName(communeName())
        .departmentCode(departmentCode())
        .oldCommuneCode(oldCommuneCode())
        .oldCommuneName(oldCommuneName())
        .plotId(plotId())
        .oldPlotId(oldPlotId())
        .volumeNumber(volumeNumber())
        .numberLot1(numberLot1())
        .surfaceSquareLot1(surfaceSquareLot1())
        .numberLot2(numberLot2())
        .surfaceSquareLot2(surfaceSquareLot2())
        .numberLot3(numberLot3())
        .surfaceSquareLot3(surfaceSquareLot3())
        .numberLot4(numberLot4())
        .surfaceSquareLot4(surfaceSquareLot4())
        .numberLot5(numberLot5())
        .surfaceSquareLot5(surfaceSquareLot5())
        .lotsCount(lotsCount())
        .localTypeCode(localTypeCode())
        .localType(localType())
        .realBuiltUpArea(realBuiltUpArea())
        .principlePiecesCount(principlePiecesCount())
        .natureCultureCode(natureCultureCode())
        .natureCulture(natureCulture())
        .specialNatureCultureCode(specialNatureCultureCode())
        .specialNatureCulture(specialNatureCulture())
        .landSurface(landSurface())
        .location(optLocation)
        .build();
  }
}
