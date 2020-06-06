# Cluster

## Nodes Information

<https://mincong.io/2020/03/07/elasticsearch-cat-nodes-api/>

The goal is to understand the current state of cluster nodes: the size of the
cluster, the node types, the load, the disk usage, the Elasticsearch version...
This is useful when operating an Elasticsearch cluster.

### Preparation

Run `docker-compose` to start an Elasticsearch cluster in localhost:

```
$ docker-compose -f src/test/resources/docker-compose.yml up
```

### Querying

Once the cluster is started, send an HTTP request to "Nodes Info API":

```
$ curl localhost:9200/_cat/nodes
172.18.0.3 31 81 3 0.16 0.39 0.45 dilm * es02
172.18.0.2 33 81 3 0.16 0.39 0.45 dilm - es03
172.18.0.4 38 81 3 0.16 0.39 0.45 dilm - es01
```

```
$ curl localhost:9200/_cat/nodes?v
ip         heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
172.18.0.3           32          81   3    0.14    0.39     0.45 dilm      *      es02
172.18.0.2           34          81   3    0.14    0.39     0.45 dilm      -      es03
172.18.0.4           39          81   3    0.14    0.39     0.45 dilm      -      es01
```

Node role "dilm" means this node is a data node (`d`), an ingest node (`i`), a machine learning
node (`l`), and a master eligible node (`m`). But it is not a coordinating node (`-`).

Need help to understand the available options? Print help:

```
$ curl -s localhost:9200/_cat/nodes?help
id                                 | id,nodeId                                   | unique node id
pid                                | p                                           | process id
ip                                 | i                                           | ip address
port                               | po                                          | bound transport port
http_address                       | http                                        | bound http address
version                            | v                                           | es version
flavor                             | f                                           | es distribution flavor
type                               | t                                           | es distribution type
build                              | b                                           | es build hash
jdk                                | j                                           | jdk version
...
```

## Cluster Settings

### API

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-get-settings.html>

```
GET /_cluster/settings
```

### Java High Level Rest Client

<https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-cluster-get-settings.html>

```java
ClusterGetSettingsRequest request = new ClusterGetSettingsRequest();
ClusterGetSettingsResponse response = client.cluster().getSettings(request, RequestOptions.DEFAULT);
```

## References

- Elastic, "Install Elasticsearch with Docker", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>
- Elastic, "Nodes Info API", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-info.html>
- Elastic, "Cat Nodes API", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-nodes.html>
