# Snapshot and Restore

Snapshot and restore in Elasticsearch.

## Prerequisite

```sh
#
# Start docker image:
# - Use single-node discovery mode to bypass the bootstrap checks
# - Use /tmp as the root path for the snapshot repositories
# - Publish port 9200 to communicate with docker image
#
docker run \
  -e discovery.type=single-node \
  -e path.repo=/tmp \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.6.2
```

## APIs

API | Method | Description
:--- | :---: | :---
`/_snapshot/` | GET | List snapshot repositories.
`/_snapshot/_status` | GET | Get all currently running snapshots with detailed status information.
`/_snapshot/{repo}` | GET | Get settings of a snapshot repository.
`/_snapshot/{repo}` | PUT | Add a new snapshot repository or edit the settings of an existing repository.
`/_snapshot/{repo}/_all` | GET | List all snapshots inside the given repository.
`/_snapshot/{repo}/_current` | GET | List all currently running snapshots inside the given repository.
`/_snapshot/{repo}/_status` | GET | Get all currently running snapshots of the given repository with detailed status information.
`/_snapshot/{repo}/{snapshot}` | GET | Get information about a single snapshot or multiple snapshots (using separator "," or wildcard expression "\*")
`/_snapshot/{repo}/{snapshot}` | DELETE | Deletes one or multiple snapshots.
`/_snapshot/{repo}/{snapshot}/_restore` | POST | Restore a snapshot of a cluster.
`/_snapshot/{repo}/{snapshot}/_status` | GET | Get a detailed description of the current state for each shard partitipcating in the snapshot.
`/_cat/snapshots/{repo}` | GET | List snapshots of a repository.
`/_cat/recovery` | GET | List all the recoveries including snapshot recoveries, including restores

### Create Snapshot Repository

Create a new snapshot repository `fs_backup` in local file-system for backup
purpose:

```
PUT /_snapshot/{repository}
{
  "type": "fs",
  "settings": {
    "location": "my_backup_location"
  }
}
```

```sh
curl -X PUT localhost:9200/_snapshot/fs_backup \
  -H 'Content-Type: application/json' \
  -d '
{
  "type": "fs",
  "settings": {
    "location": "/tmp"
  }
}'
# {"acknowledged":true}
```

Other repository backends are available in these official plugins:

- [repository-s3](https://www.elastic.co/guide/en/elasticsearch/plugins/7.7/repository-s3.html)
  for S3 repository support
- [repository-hdfs](https://www.elastic.co/guide/en/elasticsearch/plugins/7.7/repository-hdfs.html)
  for HDFS repository support in Hadoop environments
- [repository-azure](https://www.elastic.co/guide/en/elasticsearch/plugins/7.7/repository-azure.html)
  for Azure storage repositories
- [repository-gcs](https://www.elastic.co/guide/en/elasticsearch/plugins/7.7/repository-gcs.html)
  for Google Cloud Storage repositories

### Get Snapshot Repositories

Retrieve information about all registered snapshot repositories

```
GET /_snapshot
```
```
GET /_snapshot/_all
```
```sh
curl localhost:9200/_snapshot?pretty
# {
#   "fs_backup" : {
#     "type" : "fs",
#     "settings" : {
#       "location" : "/tmp"
#     }
#   }
# }
```

### Get Snapshot Repository

Retrieve information about one snapshot repository.

```
GET /_snapshot/{repository}
```

### Get Snapshots

Retrieve information about all snapshots inside one snapshot repository.

```
GET /_snapshot/{repository}/_all
```

## References

- Elastic, "Snapshot and restore | Elasticsearch Reference \[7.6\]", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/snapshot-restore.html>
