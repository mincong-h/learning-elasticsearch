# Learning Elasticsearch [![Build Status][actions-img]][actions]

Elasticsearch is a distributed, RESTful search and analytics engine capable of
addressing a growing number of use cases. As the heart of the Elastic Stack,
it centrally stores your data so you can discover the expected and uncover the
unexpected.

## Quickstart

The fastest way to test any basic Elasticsearch feature is to start a Docker image with the desired Elasticsearch version:

<!-- MANAGED_BLOCK_RUN_ES_START -->

```sh
docker run \
  --rm \
  -e discovery.type=single-node \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.12.0
```

<!-- MANAGED_BLOCK_RUN_ES_END -->

## Articles

Articles wrote using code of this repository and other related repositories:

- [Testing Elasticsearch With Docker And Java High Level REST Client](https://mincong.io/2020/04/05/testing-elasticsearch-with-docker-and-java-client/)
- [Testing Elasticsearch with ESSingleNodeTestCase](https://mincong.io/2019/11/24/essinglenodetestcase/)
- [Elasticsearch: cat nodes API](https://mincong.io/2020/03/07/elasticsearch-cat-nodes-api/)
- [Elasticsearch: Scroll API in Java](https://mincong.io/2020/01/19/elasticsearch-scroll-api/)
- [Indexing New Data in Elasticsearch](https://mincong.io/2019/12/02/indexing-new-data-in-elasticsearch/)
- [Common Index Exceptions](https://mincong.io/2020/09/13/es-index-exceptions/)
- [Wrap Elasticsearch Response Into CompletableFuture](https://mincong.io/2020/07/26/es-client-completablefuture/)
- [Discovery in Elasticsearch](https://mincong.io/2020/08/22/discovery-in-elasticsearch/)
- [GC in Elasticsearch](https://mincong.io/2020/08/30/gc-in-elasticsearch/)
- [18 Allocation Deciders in Elasticsearch](https://mincong.io/2020/09/27/shard-allocation/)
- [Using Java Time in Different Frameworks](https://mincong.io/2020/10/25/java-time/)
- [DVF: Indexing New Documents](https://mincong.io/2020/12/16/dvf-indexing/)
- [DVF: Indexing Optimization](https://mincong.io/2020/12/17/dvf-indexing-optimization/)
- [DVF: Storage Optimization](https://mincong.io/2020/12/25/dvf-storage-optimization/)
- [DVF: Snapshot And Restore](https://mincong.io/2021/01/10/dvf-snapshot-and-restore/)
- [DVF: Aggregations](https://mincong.io/2021/04/12/dvf-aggregations/)
- [DVF: Real Estate Analysis For Île-de-France in 2020](https://mincong.io/2021/04/16/dvf-real-estate-analysis-idf-2020/)
- [Disk Watermarks In Elasticsearch](https://mincong.io/2021/04/10/disk-watermarks-in-elasticsearch/)
- [Elasticsearch: Generate Configuration With Python Jinja 2](https://mincong.io/2021/04/11/elasticsearch-generate-configuration-with-python-jinja2/)
- [Fix Corrupted Index in Elasticsearch](https://mincong.io/en/elasticsearch-corrupted-index/) / [修复 Elasticsearch 中损坏的索引](https://mincong.io/cn/elasticsearch-corrupted-index/)
- [【翻译】Netflix 资产管理平台 (AMP) 中的 Elasticsearch 索引策略](https://mincong.io/cn/elasticsearch-indexing-strategy-in-asset-management-platform-amp/)
- [How to prevent data loss in Elasticsearch?](https://mincong.io/en/prevent-data-loss-in-elasticsearch/) / [如何防止 Elasticsearch 中的数据丢失？](https://mincong.io/cn/prevent-data-loss-in-elasticsearch/)
- [Elasticsearch Settings](https://mincong.io/en/elasticsearch-settings/) / [一文了解 Elasticsearch 设置](https://mincong.io/cn/elasticsearch-settings/)
- [Internal Structure Of Snapshot Repository](https://mincong.io/en/elasticsearch-snapshot-repository-structure/) / [Elasticsearch 快照仓库的内部结构](https://mincong.io/cn/elasticsearch-snapshot-repository-structure/)

## Resources

Not related to this repository, but interesting resources to read about Elasticsearch.

Elasticsearch documentation:

- [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html)
- [Java Testing Framework](https://www.elastic.co/guide/en/elasticsearch/reference/current/testing-framework.html)
- [REST APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/rest-apis.html)

Certifications:

- [Elastic Certified Engineer](https://www.elastic.co/training/elastic-certified-engineer-exam)

Books:

- Radu Gheorghe, Matthew Lee hinman, Roy Russo, "Elasticsearch in Action", Manning, 2016.
- Clinton Gormley and Zachary Tong, "Elasticsearch: The Definitive Guide", O'Reilly Media, 2014 - 2015.<br>
  <https://www.elastic.co/guide/en/elasticsearch/guide/2.x/index.html>

GitHub:

- Awesome Search<br>
  https://github.com/frutik/awesome-search
- 互联网 Java 工程师进阶知识完全扫盲 / Elasticsearch<br>
  https://github.com/doocs/advanced-java

Blogs:

- Code 972, A blog about BigData, Cloud and Search technologies by Itamar Syn-Hershko <https://code972.com/blog>
- 铭毅天下, Elasticsearch Expert, <https://elastic.blog.csdn.net/>
- 少强, Alicloud, <https://www.zhihu.com/people/8080/posts>
- 木洛, Alicloud, <https://www.zhihu.com/people/zhougong-cap/posts>

Articles:

- Michael McCandless, "Visualizing Lucene's segment merges", 2011.<br>
  <http://blog.mikemccandless.com/2011/02/visualizing-lucenes-segment-merges.html>
- Nico Tonozzi and Dumitru Daniliuc, "Reducing search indexing latency to one second", _Twitter_, 2020.<br>
  <https://blog.twitter.com/engineering/en_us/topics/infrastructure/2020/reducing-search-indexing-latency-to-one-second.html>
- Prabin Meitei M, "Garbage Collection in Elasticsearch and the G1GC", _Medium_, 2018.<br>
  <https://medium.com/naukri-engineering/garbage-collection-in-elasticsearch-and-the-g1gc-16b79a447181>

Forums:

- Elastic 中文社区 https://elasticsearch.cn/

## Development

Upgrade Elasticsearch version, e.g 7.8.0 -> 7.10.0:

```sh
> scripts/upgrade-es-version.sh 7.8.0 7.10.0
✅ pom.xml
✅ cluster/src/test/resources/docker-compose.yml
✅ README.md
Finished.
```

[actions]:     https://github.com/mincong-h/learning-elasticsearch/actions
[actions-img]: https://github.com/mincong-h/learning-elasticsearch/workflows/Actions/badge.svg
