# Cluster

## Nodes Information

<https://mincong.io/2020/03/07/elasticsearch-cat-nodes-api/>

```
GET /_cat/nodes
```

## Get Cluster Settings

### API

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-get-settings.html>

```
GET /_cluster/settings
```

### Java Legacy Client

```java
var clusterStateResponse = client.admin().cluster().prepareState().get();
var metaData = clusterStateResponse.getState().metadata();

metaData.transientSettings();
metaData.persistentSettings();
metaData.settings();
```

### Java High Level Rest Client

<https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-cluster-get-settings.html>

```java
var request = new ClusterGetSettingsRequest();
var response = client.cluster().getSettings(request, RequestOptions.DEFAULT);

response.getDefaultSettings();
response.getPersistentSettings();
response.getTransientSettings();
```

## Update Cluster Settings

### API

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html>

```
PUT /_cluster/settings
```

## Articles

- Alexander Reelsen, "Does the cluster state size impact performance?", _Elastic_, 2013.<br>
  <https://discuss.elastic.co/t/does-the-cluster-state-size-impact-performance/11344>

## References

- Elastic, "Install Elasticsearch with Docker", _Elastic_, 2020.<br>
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>
- Elastic, "Nodes Info API", _Elastic_, 2020.<br>
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-nodes-info.html>
- Elastic, "Cat Nodes API", _Elastic_, 2020.<br>
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/cat-nodes.html>
