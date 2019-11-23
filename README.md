# Learning Elasticsearch

Elasticsearch is a distributed, RESTful search and analytics engine capable of
addressing a growing number of use cases. As the heart of the Elastic Stack,
it centrally stores your data so you can discover the expected and uncover the
unexpected.

## Unit Test

<https://www.elastic.co/guide/en/elasticsearch/reference/current/unit-tests.html>

If your test is a well isolated unit test which does not need a running
Elasticsearch cluster, you can use the `ESTestCase`. If you are testing lucene
features, use `ESTestCase` and if you are testing concrete token streams, use
the `ESTokenStreamTestCase` class. Those specific classes execute additional
checks which ensure that no resources leaks are happening, after the test has
run.

## References

- Elastic, "Getting started with Elasticsearch", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html>
- Elastic, "Install Elasticsearch with Docker", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>
- Elastic, "Java Testing Framework", Elastic, 2019.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/testing-framework.html>
