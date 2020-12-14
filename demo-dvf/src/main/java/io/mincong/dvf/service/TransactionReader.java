package io.mincong.dvf.service;

import static java.util.Spliterator.ORDERED;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.Transaction;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TransactionReader {

  private final ObjectReader objectReader;

  public TransactionReader() {
    var csvMapper = new CsvMapper();
    csvMapper.registerModule(new ParameterNamesModule());
    csvMapper.registerModule(new Jdk8Module());
    csvMapper.registerModule(new JavaTimeModule());
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
