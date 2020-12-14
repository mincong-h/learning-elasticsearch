package io.mincong.dvf.service;

import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    var reader = new TransactionReader();
    reader
        .readCsv(Path.of("/Users/mincong/github/dvf/downloads/full.2020.csv"))
        .forEach(System.out::println);
  }
}
