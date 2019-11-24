# Learning Elasticsearch [![CircleCI](https://circleci.com/gh/mincong-h/learning-elasticsearch.svg?style=svg)](https://circleci.com/gh/mincong-h/learning-elasticsearch)

Elasticsearch is a distributed, RESTful search and analytics engine capable of
addressing a growing number of use cases. As the heart of the Elastic Stack,
it centrally stores your data so you can discover the expected and uncover the
unexpected.

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
