# Learning Elasticsearch [![TravisCI](https://travis-ci.org/mincong-h/learning-elasticsearch.svg?branch=master)](https://travis-ci.org/github/mincong-h/learning-elasticsearch)

Elasticsearch is a distributed, RESTful search and analytics engine capable of
addressing a growing number of use cases. As the heart of the Elastic Stack,
it centrally stores your data so you can discover the expected and uncover the
unexpected.

## Articles

- [Testing Elasticsearch With Docker And Java High Level REST Client](https://mincong.io/2020/04/05/testing-elasticsearch-with-docker-and-java-client/)
- [Testing Elasticsearch with ESSingleNodeTestCase](https://mincong.io/2019/11/24/essinglenodetestcase/)
- [Elasticsearch: cat nodes API](https://mincong.io/2020/03/07/elasticsearch-cat-nodes-api/)
- [Elasticsearch: Scroll API in Java](https://mincong.io/2020/01/19/elasticsearch-scroll-api/)
- [Indexing New Data in Elasticsearch](https://mincong.io/2019/12/02/indexing-new-data-in-elasticsearch/)

## Elasticsearch Version

How to find the version of Elasticsearch?

### Docker

Find it from the image:

```
$ docker ps
CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                            NAMES
f19c11cb116f        docker.elastic.co/elasticsearch/elasticsearch:7.5.0   "/usr/local/bin/dock…"   46 seconds ago      Up 45 seconds       0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elated_dubinsky
```

### cURL

Use cURL to send a HTTP request to a node, such as localhost:

```
$ curl localhost:9200
```
```json
{
  "name" : "f19c11cb116f",
  "cluster_name" : "es-docker-cluster",
  "cluster_uuid" : "knFvbP__SkC02BbUag8pEw",
  "version" : {
    "number" : "7.5.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "e9ccaed468e2fac2275a3761849cbee64b39519f",
    "build_date" : "2019-11-26T01:06:52.518245Z",
    "build_snapshot" : false,
    "lucene_version" : "8.3.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

## Java Client

See official documentation [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html).

## Unit Test

<https://www.elastic.co/guide/en/elasticsearch/reference/current/unit-tests.html>

If your test is a well isolated unit test which does not need a running
Elasticsearch cluster, you can use the `ESTestCase`. If you are testing lucene
features, use `ESTestCase` and if you are testing concrete token streams, use
the `ESTokenStreamTestCase` class. Those specific classes execute additional
checks which ensure that no resources leaks are happening, after the test has
run.

## JAR Hell

If you work with IntelliJ, you need to follow the official instruction [Configuring
IDEs and running tests][jar-hell]
to avoid JAR Hell. In particular:

- Set VM option `idea.no.launcher=true` in _"Help > Edit VM Options"_
- Remove `ant-javafx.jar` from the classpath of your SDK in _"Project Structure (`cmd` + `;`) > 1.8 (Java 8) > Classpath"_

If you still get JAR hell problem, see the [official link][jar-hell] to get up-to-date
instructions.

[jar-hell]: https://github.com/elastic/elasticsearch/blob/master/CONTRIBUTING.md#configuring-ides-and-running-tests

## References

- Elastic, "Getting started with Elasticsearch", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html>
- Elastic, "Install Elasticsearch with Docker", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>
- Elastic, "Java Testing Framework", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/testing-framework.html>
- Elastic, "Java High Level REST Client", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html>
