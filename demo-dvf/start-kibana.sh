#!/bin/bash
#
# Install Kibana with Docker
# https://www.elastic.co/guide/en/kibana/7.x/docker.html
#
docker_container_name_elasticsearch="elasticsearch-dvf"
docker_container_name_kibana="kibana-dvf"

docker run \
  --rm \
  -p 5601:5601 \
  --link "${docker_container_name_elasticsearch}:elasticsearch" \
  --name "$docker_container_name_kibana" \
  docker.elastic.co/kibana/kibana:7.12.0
