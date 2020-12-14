package io.mincong.dvf.service;

import static java.util.Spliterator.ORDERED;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TransactionCsvReader {

  private final ObjectReader objectReader;

  public TransactionCsvReader() {
    var csvMapper = Jackson.newCsvMapper();
    var csvSchema = csvMapper.schemaFor(Transaction.class).withHeader();
    this.objectReader = csvMapper.readerFor(Transaction.class).with(csvSchema);
  }

  public Stream<ImmutableTransaction> readCsv(Path path) {
    try {
      MappingIterator<ImmutableTransaction> iterator = objectReader.readValues(path.toFile());
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, ORDERED), false);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file " + path, e);
    }
  }
}
