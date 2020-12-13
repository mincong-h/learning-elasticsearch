package io.mincong.dvf.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.mincong.dvf.model.ImmutableTransaction;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class TransactionReaderTest {

  private final ImmutableTransaction transaction1 =
      ImmutableTransaction.builder()
          .mutationId("2020-1")
          .mutationDate(LocalDate.of(2020, 1, 7))
          .dispositionNumber("000001")
          .mutationNature("Vente")
          .propertyValue(8000)
          .addressNumber("")
          .addressSuffix("")
          .addressRoadName("FORTUNAT")
          .addressRoadCode("B063")
          .postalCode("01250")
          .communeCode("01072")
          .communeName("Ceyzériat")
          .departmentCode("01")
          .oldCommuneCode("")
          .oldCommuneName("")
          .plotId("01072000AK0216")
          .oldPlotId("")
          .volumeNumber("")
          .numberLot1("")
          .surfaceSquareLot1(Optional.empty())
          .numberLot2("")
          .surfaceSquareLot2(Optional.empty())
          .numberLot3("")
          .surfaceSquareLot3(Optional.empty())
          .numberLot4("")
          .surfaceSquareLot4(Optional.empty())
          .numberLot5("")
          .surfaceSquareLot5(Optional.empty())
          .numberOfLots(0)
          .localTypeCode("")
          .localType("")
          .realBuiltUpArea(Optional.empty())
          .principlePiecesCount(Optional.empty())
          .natureCultureCode("T")
          .natureCulture("terres")
          .specialNatureCultureCode("")
          .specialNatureCulture("")
          .landSurface(1061)
          .longitude(5.323522)
          .latitude(46.171899)
          .build();

  private final ImmutableTransaction transaction2 =
      ImmutableTransaction.builder()
          .mutationId("2020-2")
          .mutationDate(LocalDate.of(2020, 1, 7))
          .dispositionNumber("000001")
          .mutationNature("Vente")
          .propertyValue(75000)
          .addressNumber("")
          .addressSuffix("")
          .addressRoadName("RUE DE LA CHARTREUSE")
          .addressRoadCode("0064")
          .postalCode("01960")
          .communeCode("01289")
          .communeName("Péronnas")
          .departmentCode("01")
          .oldCommuneCode("")
          .oldCommuneName("")
          .plotId("01289000AI0210")
          .oldPlotId("")
          .volumeNumber("")
          .numberLot1("")
          .surfaceSquareLot1(Optional.empty())
          .numberLot2("")
          .surfaceSquareLot2(Optional.empty())
          .numberLot3("")
          .surfaceSquareLot3(Optional.empty())
          .numberLot4("")
          .surfaceSquareLot4(Optional.empty())
          .numberLot5("")
          .surfaceSquareLot5(Optional.empty())
          .numberOfLots(0)
          .localTypeCode("")
          .localType("")
          .realBuiltUpArea(Optional.empty())
          .principlePiecesCount(Optional.empty())
          .natureCultureCode("AB")
          .natureCulture("terrains a bâtir")
          .specialNatureCultureCode("")
          .specialNatureCulture("")
          .landSurface(610)
          .longitude(5.226197)
          .latitude(46.184538)
          .build();

  private final ImmutableTransaction transaction3 =
      ImmutableTransaction.builder()
          .mutationId("2020-3")
          .mutationDate(LocalDate.of(2020, 1, 14))
          .dispositionNumber("000001")
          .mutationNature("Vente")
          .propertyValue(89000)
          .addressNumber("")
          .addressSuffix("")
          .addressRoadName("VACAGNOLE")
          .addressRoadCode("B112")
          .postalCode("01340")
          .communeCode("01024")
          .communeName("Attignat")
          .departmentCode("01")
          .oldCommuneCode("")
          .oldCommuneName("")
          .plotId("01024000AL0120")
          .oldPlotId("")
          .volumeNumber("")
          .numberLot1("")
          .surfaceSquareLot1(Optional.empty())
          .numberLot2("")
          .surfaceSquareLot2(Optional.empty())
          .numberLot3("")
          .surfaceSquareLot3(Optional.empty())
          .numberLot4("")
          .surfaceSquareLot4(Optional.empty())
          .numberLot5("")
          .surfaceSquareLot5(Optional.empty())
          .numberOfLots(0)
          .localTypeCode("")
          .localType("")
          .realBuiltUpArea(Optional.empty())
          .principlePiecesCount(Optional.empty())
          .natureCultureCode("AB")
          .natureCulture("terrains a bâtir")
          .specialNatureCultureCode("")
          .specialNatureCulture("")
          .landSurface(600)
          .longitude(Optional.empty())
          .latitude(Optional.empty())
          .build();

  private Path csvPath;

  @Before
  public void setUp() {
    var classLoader = getClass().getClassLoader();
    csvPath = Path.of(classLoader.getResource("dvf-samples.csv").getFile());
  }

  @Test
  public void testTransaction() throws Exception {
    var reader = new TransactionReader();
    assertThat(reader.readCsv(csvPath))
        .contains(transaction1)
        .contains(transaction2)
        .contains(transaction3);
  }
}
