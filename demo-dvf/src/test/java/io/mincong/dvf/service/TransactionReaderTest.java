package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

public class TransactionReaderTest {

  private Path csvPath;

  @Before
  public void setUp() {
    var classLoader = getClass().getClassLoader();
    csvPath = Path.of(classLoader.getResource("dvf-samples.csv").getFile());
  }

  @Test
  public void testTransaction() {
    var reader = new TransactionReader();
    assertThat(reader.readCsv(csvPath))
        .hasSize(9)
        .contains(TRANSACTION_1)
        .contains(TRANSACTION_2)
        .contains(TRANSACTION_3)
        .contains(TRANSACTION_5);
  }
}
