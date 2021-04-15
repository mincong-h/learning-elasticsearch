package io.mincong.dvf.service;

import io.mincong.dvf.model.ImmutableTransaction;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface EsWriter {
  void createIndex();

  default CompletableFuture<Long> write(ImmutableTransaction... items) {
    return write(Stream.of(List.of(items)));
  }

  CompletableFuture<Long> write(Stream<List<ImmutableTransaction>> items);
}
