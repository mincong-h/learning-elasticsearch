package io.mincong.dvf.service;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
  public static void main(String[] args) {
    var reader = new TransactionReader();
    var map = new ConcurrentHashMap<String, String>();
    var count = new AtomicInteger();
    reader
        .readCsv(Path.of("/Users/mincong/github/dvf/downloads/full.2020.csv"))
        .forEach(
            tx -> {
              System.out.println(tx);
              map.put(tx.id(), tx.id());
              count.incrementAndGet();
            });
    System.out.println("count: " + count);
    System.out.println("map:   " + map.size());
  }
}
