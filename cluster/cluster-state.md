# Cluster State

## Get Cluster State

### API

<https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-state.html>

```
GET /_cluster/state
```
```
GET /_cluster/state/_all
```

### Legacy Client

```java
var response = client
    .admin()
    .cluster()
    .prepareState()
    // set options here ...
    .get();
```

## Components

The components of cluster state:

```
blocks
cluster_name
compressed_size_in_bytes
metadata
  cluster_uuid
  templates
  indices
  index-graveyard
  repositories
nodes
restore
routing_nodes
routing_table
snapshot_deletions
snapshots
state_uuid
version
```

Explanation:

Name | Description
:--- | :---
blocks | Description, retry-ability, permission (read/write) of the index and its metadata
cluster\_name | Name of the cluster
compressed\_size\_in\_bytes | Maybe compressed size of cluster state in bytes
metadata | Metadata
nodes | Nodes of the cluster: name, id, IP address, attributes
restore | Restore operations on snapshots.
routing\_nodes | Unassigned shards (snapshot restore), routing nodes
routing\_table | Routing indices with shards info
snapshot\_deletions | In-progress snapshot deletions
snapshots | In-progress snapshot creations
state\_uuid | UUID of the cluster state
version | Version of the cluster state

Inside metadata section:

Name | Description
:--- | :---
cluster\_uuid | UUID of the cluster state
templates | Security index templates, machine-learning metadata/state/config, logstash index template etc
indices | Index state, settings, mappings, alias, primary terms, allocations, etc
index-graveyard | ?
repositories | Snapshot repositories with type and settings

## Reduce Network I/O

Clear all the options before performing the actual call using `clear()` to
avoid heavy network I/O. Useful when the cluster state is important:

```java
var clusterState = client
    .admin()
    .cluster()
    .prepareState()
    .clear()
    /*
     * Define your options explicitly here as:
     *
     *     setXxx(...)
     */
    .setCustoms(true)
    .get()
    .getState();
```
