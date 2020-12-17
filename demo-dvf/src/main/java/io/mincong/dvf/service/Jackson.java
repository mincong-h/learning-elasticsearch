package io.mincong.dvf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class Jackson {

  public static CsvMapper newCsvMapper() {
    var csvMapper = new CsvMapper();
    csvMapper.registerModule(new ParameterNamesModule());
    csvMapper.registerModule(new Jdk8Module());
    csvMapper.registerModule(new JavaTimeModule());
    csvMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return csvMapper;
  }

  public static ObjectMapper newObjectMapper() {
    var objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ParameterNamesModule());
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
