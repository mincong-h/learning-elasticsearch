# Snapshot and Restore

Snapshot and restore in Elasticsearch.

## Prerequisite

```sh
#
# Start docker image:
# - Use env variable discovery.type=single-node to bypass the bootstrap checks
# - Use env variable path.repo=/tmp as the root path for the snapshot repositories
# - Publish port 9200 to communicate with docker image
docker run \
  -e discovery.type=single-node \
  -e path.repo=/tmp \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.6.2
```

## APIs

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
