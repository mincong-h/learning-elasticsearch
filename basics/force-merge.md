# Force-Merge

## Prerequisite

Start a single-node Elasticsearch cluster in localhost:

```sh
docker run \
  --rm \
  -e discovery.type=single-node \
  -p 9200:9200 \
  docker.elastic.co/elasticsearch/elasticsearch:7.9.1
```

## Merge One Index

> :warning: The results of this scenario is not always the same. The number of
> segments may be different at each step.

Index document `msg_1`:

```sh
curl -X PUT http://localhost:9200/my_index/_doc/msg_1?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "Hello world!" }'
```

```json
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "msg_1",
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

Index another document `msg_2`:

```sh
curl -X PUT http://localhost:9200/my_index/_doc/msg_2?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "Hello Elasticsearch!" }'
```

```json
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "msg_2",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

Observe the segments:

```sh
curl http://localhost:9200/my_index/_segments?pretty
```

```json
{
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "indices" : {
    "my_index" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "N5yl72fESACStTPy7PW2xg"
            },
            "num_committed_segments" : 0,
            "num_search_segments" : 2,
            "segments" : {
              "_0" : {
                "generation" : 0,
                "num_docs" : 1,
                "deleted_docs" : 0,
                "size_in_bytes" : 3632,
                "memory_in_bytes" : 1364,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              },
              "_1" : {
                "generation" : 1,
                "num_docs" : 1,
                "deleted_docs" : 0,
                "size_in_bytes" : 3691,
                "memory_in_bytes" : 1364,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    }
  }
}
```

Delete the last document indexed ("Hello Elasticsearch!"):

```sh
curl -X DELETE http://localhost:9200/my_index/_doc/msg_2?pretty
```

```json
{
  "_index" : "my_index",
  "_type" : "_doc",
  "_id" : "msg_2",
  "_version" : 2,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 2,
  "_primary_term" : 1
}
```

Observe the segments:

```sh
curl http://localhost:9200/my_index/_segments?pretty
```

```js
{
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "indices" : {
    "my_index" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "N5yl72fESACStTPy7PW2xg"
            },
            "num_committed_segments" : 0,
            "num_search_segments" : 3,
            "segments" : {
              "_0" : {
                "generation" : 0,
                "num_docs" : 1,
                "deleted_docs" : 0,
                "size_in_bytes" : 3632,
                "memory_in_bytes" : 1364,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              },
              "_1" : {
                "generation" : 1,
                "num_docs" : 0,
                "deleted_docs" : 1,
                "size_in_bytes" : 4754,
                "memory_in_bytes" : 1564,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              },
              "_2" : {
                "generation" : 2,
                "num_docs" : 0,
                "deleted_docs" : 1,
                "size_in_bytes" : 3039,
                "memory_in_bytes" : 852,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    }
  }
}
```

Force-merge documents. Set the `max_num_segments` to 1 so that we can fully
merge indices:

```sh
curl -X POST "http://localhost:9200/my_index/_forcemerge?max_num_segments=1&pretty"
```

```json
{
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  }
}
```

Observe the segments multiple times in the next minutes. We need to observe
multiple times because automatic merge may also happen. At the end, the segments
should look likes the following:

```sh
curl http://localhost:9200/my_index/_segments?pretty
```

```js
{
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "indices" : {
    "my_index" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "N5yl72fESACStTPy7PW2xg"
            },
            "num_committed_segments" : 1,
            "num_search_segments" : 1,
            "segments" : {                 // Segments _0, _1, _2 are removed
              "_3" : {                     // This segment is created after calling the Force Merge API
                "generation" : 3,          // Elasticsearch incremented the generation
                "num_docs" : 1,            // 1 = 1 + 0 + 0
                "deleted_docs" : 2,        // 2 = 0 + 1 + 1
                "size_in_bytes" : 3869,
                "memory_in_bytes" : 1364,
                "committed" : true,        // It is the only segment committed. It means that the segment
                                           // is synced to disk and can survive a hard reboot
                "search" : true,           // It is searcheable because segments _0, _1, _2 are removed
                "version" : "8.6.2",
                "compound" : false,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    }
  }
}
``` 

## Merge Multiple Indices

Create two documents `email_1` and `email_2` in index `emails` and then delete
one:

```sh
curl -X PUT http://localhost:9200/emails/_doc/email_1?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "A meaningful email." }'

curl -X PUT http://localhost:9200/emails/_doc/email_2?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "An awesome email." }'

curl -X DELETE http://localhost:9200/emails/_doc/email_2?pretty
```

Create two documents `fruit_1` and `fruit_2` in index `fruits`:

```sh
curl -X PUT http://localhost:9200/fruits/_doc/fruit_1?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "Apple" }'

curl -X PUT http://localhost:9200/fruits/_doc/fruit_2?pretty \
  -H 'Content-Type: application/json' \
  -d '{ "msg" : "Banana" }'

curl -X DELETE http://localhost:9200/fruits/_doc/fruit_2?pretty
```

Observe segments:

```sh
curl http://localhost:9200/emails,fruits/_segments?pretty
```

```json
{
  "_shards" : {
    "total" : 4,
    "successful" : 2,
    "failed" : 0
  },
  "indices" : {
    "emails" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "7ZSUF4CYTCupAtlZ2ItK4g"
            },
            "num_committed_segments" : 0,
            "num_search_segments" : 2,
            "segments" : {
              "_0" : {
                "generation" : 0,
                "num_docs" : 1,
                "deleted_docs" : 1,
                "size_in_bytes" : 4873,
                "memory_in_bytes" : 1564,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              },
              "_1" : {
                "generation" : 1,
                "num_docs" : 0,
                "deleted_docs" : 1,
                "size_in_bytes" : 3047,
                "memory_in_bytes" : 852,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    },
    "fruits" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "7ZSUF4CYTCupAtlZ2ItK4g"
            },
            "num_committed_segments" : 0,
            "num_search_segments" : 2,
            "segments" : {
              "_0" : {
                "generation" : 0,
                "num_docs" : 1,
                "deleted_docs" : 1,
                "size_in_bytes" : 4742,
                "memory_in_bytes" : 1564,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              },
              "_1" : {
                "generation" : 1,
                "num_docs" : 0,
                "deleted_docs" : 1,
                "size_in_bytes" : 3047,
                "memory_in_bytes" : 852,
                "committed" : false,
                "search" : true,
                "version" : "8.6.2",
                "compound" : true,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    }
  }
}
```

Force merge

```sh
curl -X POST "http://localhost:9200/emails,fruits/_forcemerge?max_num_segments=1&pretty"
```

```js
{
  "_shards" : {
    "total" : 4,
    "successful" : 2,
    "failed" : 0
  }
}
```

Observe segments again. We can see that both indices `emails` and `fruits` are
optimized by force merging existing segments into a new one. However, they do
not share the same segments. Each index have its segments.

```sh
curl http://localhost:9200/emails,fruits/_segments?pretty
```

```js
{
  "_shards" : {
    "total" : 4,
    "successful" : 2,
    "failed" : 0
  },
  "indices" : {
    "emails" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "7ZSUF4CYTCupAtlZ2ItK4g"
            },
            "num_committed_segments" : 1,
            "num_search_segments" : 1,
            "segments" : {
              "_2" : {
                "generation" : 2,
                "num_docs" : 1,
                "deleted_docs" : 0,
                "size_in_bytes" : 3556,
                "memory_in_bytes" : 1364,
                "committed" : true,
                "search" : true,
                "version" : "8.6.2",
                "compound" : false,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    },
    "fruits" : {
      "shards" : {
        "0" : [
          {
            "routing" : {
              "state" : "STARTED",
              "primary" : true,
              "node" : "7ZSUF4CYTCupAtlZ2ItK4g"
            },
            "num_committed_segments" : 1,
            "num_search_segments" : 1,
            "segments" : {
              "_2" : {
                "generation" : 2,
                "num_docs" : 1,
                "deleted_docs" : 0,
                "size_in_bytes" : 3464,
                "memory_in_bytes" : 1364,
                "committed" : true,
                "search" : true,
                "version" : "8.6.2",
                "compound" : false,
                "attributes" : {
                  "Lucene50StoredFieldsFormat.mode" : "BEST_SPEED"
                }
              }
            }
          }
        ]
      }
    }
  }
}
```

## Segment Information

_Where can we find the information about segments in an Elasticsearch cluster?_

This information can be retrieved from index stats:

```sh
curl http://localhost:9200/emails/_stats?pretty
```

```js
{
  "indices" : {
    "emails" : {
      "uuid" : "PpxyqAdgTOSA2qsODvaOOw",
      "primaries" : {
        "merges" : {
          "current" : 0,
          "current_docs" : 0,
          "current_size_in_bytes" : 0,
          "total" : 1,
          "total_time_in_millis" : 39,
          "total_docs" : 2,
          "total_size_in_bytes" : 4873,
          "total_stopped_time_in_millis" : 0,
          "total_throttled_time_in_millis" : 0,
          "total_auto_throttle_in_bytes" : 20971520
        },
        "segments" : {
          "count" : 1,
          "memory_in_bytes" : 1364,
          "terms_memory_in_bytes" : 736,
          "stored_fields_memory_in_bytes" : 488,
          "term_vectors_memory_in_bytes" : 0,
          "norms_memory_in_bytes" : 64,
          "points_memory_in_bytes" : 0,
          "doc_values_memory_in_bytes" : 76,
          "index_writer_memory_in_bytes" : 0,
          "version_map_memory_in_bytes" : 0,
          "fixed_bit_set_memory_in_bytes" : 0,
          "max_unsafe_auto_id_timestamp" : -1,
          "file_sizes" : { }
        },
        ...
      },
      "total" : {
        "merges" : {
          "current" : 0,
          "current_docs" : 0,
          "current_size_in_bytes" : 0,
          "total" : 1,
          "total_time_in_millis" : 39,
          "total_docs" : 2,
          "total_size_in_bytes" : 4873,
          "total_stopped_time_in_millis" : 0,
          "total_throttled_time_in_millis" : 0,
          "total_auto_throttle_in_bytes" : 20971520
        },
        "segments" : {
          "count" : 1,
          "memory_in_bytes" : 1364,
          "terms_memory_in_bytes" : 736,
          "stored_fields_memory_in_bytes" : 488,
          "term_vectors_memory_in_bytes" : 0,
          "norms_memory_in_bytes" : 64,
          "points_memory_in_bytes" : 0,
          "doc_values_memory_in_bytes" : 76,
          "index_writer_memory_in_bytes" : 0,
          "version_map_memory_in_bytes" : 0,
          "fixed_bit_set_memory_in_bytes" : 0,
          "max_unsafe_auto_id_timestamp" : -1,
          "file_sizes" : { }
        },
        ...
      }
    }
  }
}
```

## Thread Pool

Force merge operations are done usingi thread pool `force_merge`, which is a
fixed thread pool with a size of 1 and an unbounded queue size.

```
~ $ curl -s "http://localhost:9200/_cluster/settings?flat_settings&include_defaults&pretty" | grep force_merge
    "enrich.max_force_merge_attempts" : "3",
    "thread_pool.force_merge.queue_size" : "-1",
    "thread_pool.force_merge.size" : "1",
```

## Tasks

To monitor long-running force-merge tasks, you can use the Task Management API
to fetch the actions `indices:admin/forcemerge`. Since Elasticsearch 7.4.0, if
you provide query parameter `detailed`, a description is also attached the task
explain the indices being force-merged ([PR](https://github.com/elastic/elasticsearch/pull/41365)):

```
curl "localhost:9200/_tasks?actions=indices:admin/forcemerge*&detailed"
```

```js
"bWByk2_lTGKufmq24Inu9g:418" : {
   "node" : "bWByk2_lTGKufmq24Inu9g",
   "action" : "indices:admin/forcemerge",
   "id" : 418,
   "headers" : {},
   "cancellable" : false,
   "running_time_in_nanos" : 161112867379,
   "description" : "Force-merge indices[twitter], maxSegments[-1], onlyExpungeDeletes[false], flush[true]",
   "start_time_in_millis" : 1555624171922,
   "type" : "transport"
}
```

## References

- Force Merge API
  <https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-forcemerge.html>
- How to Clean and Optimize Elasticsearch Indices
  <https://kb.objectrocket.com/elasticsearch/how-to-clean-and-optimize-elasticsearch-indices-258>
- Thread pools
  <https://www.elastic.co/guide/en/elasticsearch/reference/7.9/modules-threadpool.html>
- Show force merge/optimize progress
  <https://github.com/elastic/elasticsearch/issues/15975>
