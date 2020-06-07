# Cluster Settings

Settings can be persistent, meaning they apply across restarts, or transient,
where they don’t survive a full cluster restart.

## Get Settings

### API

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-get-settings.html>

```
GET /_cluster/settings
```

### Java Legacy Client

```java
var clusterStateResponse = client.admin().cluster().prepareState().get();
var metaData = clusterStateResponse.getState().getMetaData();

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

## Update Settings

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html>

```
PUT /_cluster/settings
```

## Persistent Settings

Persistent settings are stored on each master-eligible node in the global
cluster state file, which can be found in the Elasticsearch data directory:
`data/CLUSTER_NAME/nodes/N/_state`, where `CLUSTER_NAME` is the name of the
cluster and `N` is the node number (0 if this is the only node on this
machine). The file name has the following format: `global-NNN` where `NNN` is
the version of the cluster state.

Besides persistent settings, this file may contain other global metadata such as
index templates. By default the global cluster state file is stored in the
binary SMILE format. For debugging purposes, if you want to see what's actually
stored in this file, you can change the format of this file to JSON by adding
the following line to the `elasticsearch.yml` file:

    format: json

Every time cluster state changes, all master-eligible nodes store the new
version of the file, so during cluster restart the node that starts first and
elects itself as a master will have the newest version of the cluster state.

## References

- imotov, "Where does ElasticSearch store persistent settings?", _Stack Overflow_, 2014.
  <https://stackoverflow.com/questions/21804987/>
- Elastic, "Cluster update settings API", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-update-settings.html>
