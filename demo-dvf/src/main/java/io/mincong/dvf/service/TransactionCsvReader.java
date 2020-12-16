package io.mincong.dvf.service;

import static java.util.Spliterator.ORDERED;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import io.mincong.dvf.model.ImmutableTransaction;
import io.mincong.dvf.model.ImmutableTransactionRow;
import io.mincong.dvf.model.TransactionRow;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TransactionCsvReader {

  private static final int BULK_SIZE = 1_000;
  private final ObjectReader objectReader;

  public TransactionCsvReader() {
    var csvMapper = Jackson.newCsvMapper();
    var csvSchema = csvMapper.schemaFor(TransactionRow.class).withHeader();
    this.objectReader = csvMapper.readerFor(TransactionRow.class).with(csvSchema);
  }

  public Stream<List<ImmutableTransaction>> readCsv(Path path) {
    try {
      MappingIterator<ImmutableTransactionRow> iterator = objectReader.readValues(path.toFile());
      var bulkIterator = new BulkIterator<>(iterator, BULK_SIZE);
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(bulkIterator, ORDERED), false)
          .map(
              rows ->
                  rows.stream().map(TransactionRow::toTransactionObj).collect(Collectors.toList()));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read file " + path, e);
    }
  }

  static class BulkIterator<T> implements Iterator<List<T>> {
    private final Iterator<T> iterator;
    private final int bulkSize;

    public BulkIterator(Iterator<T> iterator, int bulkSize) {
      this.iterator = iterator;
      this.bulkSize = bulkSize;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public List<T> next() {
      var results = new ArrayList<T>(bulkSize);
      while (hasNext() && results.size() < bulkSize) {
        results.add(iterator.next());
      }
      if (results.isEmpty()) {
        throw new NoSuchElementException();
      }
      return results;
    }
  }
}
