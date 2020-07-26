# Learning Elasticsearch [![TravisCI](https://travis-ci.org/mincong-h/learning-elasticsearch.svg?branch=master)](https://travis-ci.org/github/mincong-h/learning-elasticsearch)

Elasticsearch is a distributed, RESTful search and analytics engine capable of
addressing a growing number of use cases. As the heart of the Elastic Stack,
it centrally stores your data so you can discover the expected and uncover the
unexpected.

## Quickstart

The fastest way to test any basic Elasticsearch feature is to start a Docker image with the desired Elasticsearch version:

```sh
docker run \
  --rm \
  -e discovery.type=single-node \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.8.0
```

## Articles

Articles wrote using code of this repository:

- [Testing Elasticsearch With Docker And Java High Level REST Client](https://mincong.io/2020/04/05/testing-elasticsearch-with-docker-and-java-client/)
- [Testing Elasticsearch with ESSingleNodeTestCase](https://mincong.io/2019/11/24/essinglenodetestcase/)
- [Elasticsearch: cat nodes API](https://mincong.io/2020/03/07/elasticsearch-cat-nodes-api/)
- [Elasticsearch: Scroll API in Java](https://mincong.io/2020/01/19/elasticsearch-scroll-api/)
- [Indexing New Data in Elasticsearch](https://mincong.io/2019/12/02/indexing-new-data-in-elasticsearch/)
- [Wrap Elasticsearch Response Into CompletableFuture](https://mincong.io/2020/07/26/es-client-completablefuture/)

## Resources

- Elasticsearch: [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
- Elasticsearch: [Java Testing Framework](https://www.elastic.co/guide/en/elasticsearch/reference/current/testing-framework.html)
- Elasticsearch: [REST APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/rest-apis.html)

## Other Resources

Not related to this repository, but interesting articles to read about Elasticsearch:

-  Nico Tonozzi and Dumitru Daniliuc, "Reducing search indexing latency to one second", _Twitter_, 2020.<br>
   <https://blog.twitter.com/engineering/en_us/topics/infrastructure/2020/reducing-search-indexing-latency-to-one-second.html>
