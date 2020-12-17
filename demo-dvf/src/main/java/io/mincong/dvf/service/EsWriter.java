package io.mincong.dvf.service;

import io.mincong.dvf.model.ImmutableTransaction;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface EsWriter {
  void createIndex(String name);

  default CompletableFuture<List<String>> write(ImmutableTransaction... items) {
    return write(Stream.of(List.of(items)));
  }

  CompletableFuture<List<String>> write(Stream<List<ImmutableTransaction>> items);
}
