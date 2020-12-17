package io.mincong.dvf.service;

import static io.mincong.dvf.model.TestModels.*;
import static org.assertj.core.api.Assertions.assertThat;

import io.mincong.dvf.service.TransactionCsvReader.BulkIterator;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import org.assertj.core.api.Assertions;
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
    var reader = new TransactionCsvReader(2000);
    assertThat(reader.readCsv(csvPath))
        .hasSize(1)
        .flatExtracting(Function.identity())
        .contains(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4, TRANSACTION_5);
  }

  @Test
  public void testBulkIterator2() {
    var transactions =
        List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4, TRANSACTION_5);
    var iterator = new BulkIterator<>(transactions.iterator(), 2);
    Assertions.assertThat(iterator)
        .toIterable()
        .containsExactly(
            List.of(TRANSACTION_1, TRANSACTION_2),
            List.of(TRANSACTION_3, TRANSACTION_4),
            List.of(TRANSACTION_5));
  }

  @Test
  public void testBulkIterator3() {
    var transactions =
        List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4, TRANSACTION_5);
    var iterator = new BulkIterator<>(transactions.iterator(), 3);
    Assertions.assertThat(iterator)
        .toIterable()
        .containsExactly(
            List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3),
            List.of(TRANSACTION_4, TRANSACTION_5));
  }

  @Test
  public void testBulkIterator4() {
    var transactions =
        List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4, TRANSACTION_5);
    var iterator = new BulkIterator<>(transactions.iterator(), 4);
    Assertions.assertThat(iterator)
        .toIterable()
        .containsExactly(
            List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3, TRANSACTION_4),
            List.of(TRANSACTION_5));
  }
}
