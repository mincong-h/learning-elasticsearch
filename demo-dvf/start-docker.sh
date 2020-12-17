#!/bin/bash

esdata="${HOME}/dvf-volume-es"

if [ ! -d "$esdata" ]
then
  mkdir -p "$esdata"
fi

docker run \
  --rm \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "cluster.name=es-docker-cluster" \
  -v "$esdata":/usr/share/elasticsearch/data \
  docker.elastic.co/elasticsearch/elasticsearch:7.10.1
