#!/bin/bash

esdata="${HOME}/dvf-volume-es"
esbackup="${HOME}/es-backup/demo-dvf/"

# How can I get the source directory of a Bash script from within the script itself?
# https://stackoverflow.com/questions/59895/
current_dir="$(cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)"

docker_container_name_elasticsearch="elasticsearch-dvf"

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
  -v "${current_dir}/src/main/resources/config/custom.elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml" \
  --name "$docker_container_name_elasticsearch" \
  docker.elastic.co/elasticsearch/elasticsearch:7.12.0
