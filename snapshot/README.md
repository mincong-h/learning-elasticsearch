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

Create a new snapshot repository `fs_backup` in local file-system for backup
purpose:

### Create Snapshot Repository

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

## References

- Elastic, "Snapshot and restore | Elasticsearch Reference \[7.6\]", _Elastic_, 2020.
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/snapshot-restore.html>
