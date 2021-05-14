#!/bin/bash

# How can I get the source directory of a Bash script from within the script itself?
# https://stackoverflow.com/questions/59895/
current_dir="$(cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)"

docker run \
  --rm \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "cluster.name=es-docker-cluster" \
  -v "${current_dir}/../demo-dvf/src/main/resources/config/custom.elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml" \
  docker.elastic.co/elasticsearch/elasticsearch:7.12.0
