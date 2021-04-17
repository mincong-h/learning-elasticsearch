# DVF

## Articles

1. [Indexing New Documents](https://mincong.io/2020/12/16/dvf-indexing/) -- Indexing new documents into Elasticsearch using French government's open data "Demande de valeurs foncières (DVF)".
2. [Indexing Optimization](https://mincong.io/2020/12/17/dvf-indexing-optimization/) -- Optimize the indexing process using bulk index requests and multi-threading. 
3. [Storage Optimization](https://mincong.io/2020/12/25/dvf-storage-optimization/) -- How to optmize storage of a given index by 40% using force-merge. 
4. [Snapshot And Restore](https://mincong.io/2021/01/10/dvf-snapshot-and-restore/) -- How to create a snapshot for index "transactions" of DVF and restore it to an Elasticsearch cluster.
5. [Aggregations](https://mincong.io/2021/04/12/dvf-aggregations/) -- How to write and execute metric and bucket aggregations in Elasticsearch for data analytics. Also, how to execute aggregations that contain sub-aggregations.
6. [Real Estate Analysis For Île-de-France in 2020](https://mincong.io/2021/04/16/dvf-real-estate-analysis-idf-2020/) -- This article studies the real estate market of Île-de-France in 2020 by exploring and visualizing the dataset DVF using Kibana. We will discuss the global landscape, the impact of COVID-19, the situation in different departments, and more.

## Frontend

### elasticsearch-head

Use <https://github.com/mobz/elasticsearch-head>.

### Kibana

Run commands:

```
./start-elasticsearch.sh
./start-kibana.sh
```

Then visit Kibana: http://localhost:5601
