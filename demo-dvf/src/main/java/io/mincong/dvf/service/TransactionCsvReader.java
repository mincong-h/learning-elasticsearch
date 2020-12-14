package io.mincong.dvf.service;

import static java.util.Spliterator.ORDERED;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.ImmutableTransactionRow;
import io.mincong.dvf.model.TransactionRow;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TransactionCsvReader {

  private final ObjectReader objectReader;

  public TransactionCsvReader() {
    var csvMapper = Jackson.newCsvMapper();
    var csvSchema = csvMapper.schemaFor(TransactionRow.class).withHeader();
    this.objectReader = csvMapper.readerFor(TransactionRow.class).with(csvSchema);
  }

  public Stream<ImmutableTransaction> readCsv(Path path) {
    try {
      MappingIterator<ImmutableTransactionRow> iterator = objectReader.readValues(path.toFile());
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false)
          .map(TransactionRow::toTransactionObj);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file " + path, e);
    }
  }
}
