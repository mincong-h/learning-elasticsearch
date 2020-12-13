package io.mincong.dvf.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.mincong.dvf.model.*;
import java.nio.file.Path;
import java.time.LocalDate;
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
          .surfaceSquareLot1(0.0) // FIXME optional
          .numberLot2("") // FIXME optional
          .surfaceSquareLot2(0.0) // FIXME optional
          .numberLot3("") // FIXME optional
          .surfaceSquareLot3(0.0) // FIXME optional
          .numberLot4("") // FIXME optional
          .surfaceSquareLot4(0.0) // FIXME optional
          .numberLot5("") // FIXME optional
          .surfaceSquareLot5(0.0) // FIXME optional
          .numberOfLots(0)
          .localTypeCode("")
          .localType("")
          .realBuiltUpArea(0.0) // FIXME optional
          .principlePiecesCount(0) // FIXME optional
          .natureCultureCode("T")
          .natureCulture("terres")
          .specialNatureCultureCode("") // FIXME optional
          .specialNatureCulture("") // FIXME optional
          .landSurface("1061") // FIXME type
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
          .surfaceSquareLot1(0.0) // FIXME optional
          .numberLot2("") // FIXME optional
          .surfaceSquareLot2(0.0) // FIXME optional
          .numberLot3("") // FIXME optional
          .surfaceSquareLot3(0.0) // FIXME optional
          .numberLot4("") // FIXME optional
          .surfaceSquareLot4(0.0) // FIXME optional
          .numberLot5("") // FIXME optional
          .surfaceSquareLot5(0.0) // FIXME optional
          .numberOfLots(0)
          .localTypeCode("")
          .localType("")
          .realBuiltUpArea(0.0) // FIXME optional
          .principlePiecesCount(0) // FIXME optional
          .natureCultureCode("AB")
          .natureCulture("terrains a bâtir")
          .specialNatureCultureCode("") // FIXME optional
          .specialNatureCulture("") // FIXME optional
          .landSurface("610") // FIXME type
          .longitude(5.226197)
          .latitude(46.184538)
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
    assertThat(reader.readCsv(csvPath)).contains(transaction1, transaction2);
  }
}
