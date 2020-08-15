# GC

## Flag Lookup

Find the default value of a GC setting in Java:

```sh
java -XX:+PrintFlagsFinal -version
```

For example, finding the default value of `MaxGCPauseMillis` can be done as follow:

```sh
~ $ java -XX:+PrintFlagsFinal -version | grep MaxGCPauseMillis
    uintx MaxGCPauseMillis                         = 200                                       {product} {default}
openjdk version "14.0.2" 2020-07-14
OpenJDK Runtime Environment AdoptOpenJDK (build 14.0.2+12)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 14.0.2+12, mixed mode, sharing)
```

## Articles

- Elasticsearch, "GC logging", _Elasticsearch_, 2020.<br>
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/gc-logging.html>
- Oracle, "Concurrent Mark Sweep (CMS) Collector (Java 8)", _Oracle_, 2020.<br>
  <https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/cms.html>
- Prabin Meitei M, "Garbage Collection in Elasticsearch and the G1GC", _Medium_, 2018.<br>
  <https://medium.com/naukri-engineering/garbage-collection-in-elasticsearch-and-the-g1gc-16b79a447181>
