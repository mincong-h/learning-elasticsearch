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
