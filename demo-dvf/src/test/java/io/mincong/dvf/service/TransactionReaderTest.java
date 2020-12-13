package io.mincong.dvf.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class TransactionReaderTest {

  private Path csvPath;

  private Transaction transaction1 =
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

  @Before
  public void setUp() {
    ClassLoader classLoader = getClass().getClassLoader();
    csvPath = Path.of(classLoader.getResource("dvf-samples.csv").getFile());
  }

  @Test
  public void testTransaction() throws Exception {
    var reader = new TransactionReader();
    assertThat(reader.readCsv(csvPath)).isEmpty();
  }
}
