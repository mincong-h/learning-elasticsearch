#!/bin/bash

esdata="${HOME}/dvf-volume-es"
esbackup="${HOME}/es-backup/demo-dvf/"

if [ ! -d "$esdata" ]
then
  mkdir -p "$esdata"
fi

if [ ! -d "$esbackup" ]
then
  mkdir -p "$esbackup"
fi

docker run \
  --rm \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "cluster.name=es-docker-cluster" \
  -e "path.repo=/opt/elasticsearch/backup" \
  -v "$esdata":/usr/share/elasticsearch/data \
  -v "$esbackup":/opt/elasticsearch/backup \
  docker.elastic.co/elasticsearch/elasticsearch:7.12.0
