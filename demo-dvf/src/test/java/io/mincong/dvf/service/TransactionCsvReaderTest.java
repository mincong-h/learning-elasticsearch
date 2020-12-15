package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

public class TransactionCsvReaderTest {

  private Path csvPath;

  @Before
  public void setUp() {
    var classLoader = getClass().getClassLoader();
    csvPath = Path.of(classLoader.getResource("dvf-samples.csv").getFile());
  }

  @Test
  public void testTransaction() {
    var reader = new TransactionCsvReader();
    assertThat(reader.readCsv(csvPath))
        .hasSize(9)
        .contains(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4, TRANSACTION_5);
  }
}
