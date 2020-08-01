# Cluster Stats

## Get Cluster Stats

https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-stats.html

The Cluster Stats API allows to retrieve statistics from a cluster wide perspective. The API returns
basic index metrics (shard numbers, store size, memory usage) and information about the current
nodes that form the cluster (number, roles, os, jvm versions, memory usage, cpu and installed
plugins).

See section "Response Body" of the link above the see the JSON structure and available metrics and information.

### API

```
GET /_cluster/stats
```

```
GET /_cluster/stats/nodes/<node_filter>
```

## Java Legacy Client

```java
var clusterStatsResponse = client.admin().cluster().prepareClusterStats().get();

// query value of `nodes.fs.available`
var availableByteSize = clusterStatsResponse.getNodesStats().getFs().getAvailable();
```

## References

- Korhan Herguner, "How to high level rest client request request nodes stats URGENT!", _Elastic Discuss_, 2019.<br>
  https://discuss.elastic.co/t/how-to-high-level-rest-client-request-request-nodes-stats-urgent/170324
