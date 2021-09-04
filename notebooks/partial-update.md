# Partial Update Document

Can Elasticsearch handle partial update for documents?

## Start Elasticsearch

```
docker run \
  --rm \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "cluster.name=es-docker-cluster" \
  docker.elastic.co/elasticsearch/elasticsearch:7.14.0
```

## Create new document

Request:

```sh
curl -X PUT http://localhost:9200/my_index/_doc/1?pretty \
  -H 'Content-Type: application/json' \
  -d '{"key1": "value1"}'
```

Response:

```json
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 0,
  "_primary_term" : 1
}
```

## Update existing document

Request:

```sh
curl -X POST 'http://localhost:9200/my_index/_update/1?pretty' \
  -H 'Content-Type: application/json' \
  -d '{
   "doc": {
        "key2": "value2"
   }
}'
```

Response:

```json
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

Result:

```
curl 'http://localhost:9200/my_index/_doc/1?pretty'
```

```js
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "_seq_no" : 1,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "key1" : "value1", // both key1 and key2 are available
    "key2" : "value2"
  }
}
```