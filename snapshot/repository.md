# Snapshot Repository

Snapshot repository understanding in Elasticsearch.

## Prerequisite

See <https://mincong.io/2021/01/10/dvf-snapshot-and-restore/>

## Index

There is one index "transactions" in the cluster:

```sh
curl -s localhost:9200/_cat/indices
```

```
yellow open transactions P59v6s2PS-GnaCrXgPv2Zg 1 1 10000 0 2.5mb 2.5mb
```

According to the settings of this index, we can see that
`P59v6s2PS-GnaCrXgPv2Zg` is the UUID of the index:

```sh
curl -s localhost:9200/transactions/_settings | jq
```

```json
{
  "transactions": {
    "settings": {
      "index": {
        "routing": {
          "allocation": {
            "include": {
              "_tier_preference": "data_content"
            }
          }
        },
        "number_of_shards": "1",
        "provided_name": "transactions",
        "creation_date": "1610896271859",
        "number_of_replicas": "1",
        "uuid": "P59v6s2PS-GnaCrXgPv2Zg",
        "version": {
          "created": "7100199"
        }
      }
    }
  }
}
```

## Snapshots

```sh
curl -s localhost:9200/_snapshot/dvf/_all | jq
```

```json
{
  "snapshots": [
    {
      "snapshot": "transactions.2021-01-10",
      "uuid": "DsseRXnjTwC_tcV0-VgAww",
      "version_id": 7100199,
      "version": "7.10.1",
      "indices": [
        "transactions"
      ],
      "data_streams": [],
      "include_global_state": false,
      "state": "SUCCESS",
      "start_time": "2021-01-17T15:11:28.179Z",
      "start_time_in_millis": 1610896288179,
      "end_time": "2021-01-17T15:11:28.981Z",
      "end_time_in_millis": 1610896288981,
      "duration_in_millis": 802,
      "failures": [],
      "shards": {
        "total": 1,
        "failed": 0,
        "successful": 1
      }
    }
  ]
}
```

## Repository

_What is the structure of the snapshot repository?_

By inspecting the directory of the repository "dvf", we can see the content as
below:

```sh
#
# > pwd
# /Users/minconghuang/es-backup/demo-dvf/dvf
#
tree .
.
├── index-4
├── index.latest
├── indices
│   └── toVoOSewT8eO7PbggT7SaA
│       ├── 0
│       │   ├── __7wvBjFfGSouvbPEOI53iMg
│       │   ├── __AsxBUR80T3u6_HCXfAoUjg
│       │   ├── __EjbTOvH4SDOx782QF8L3Ag
│       │   ├── __I3G4iKJ8QvmG44-mX-qw_w
│       │   ├── __KfAc01JpQHW2p9qMTMkeYA
│       │   ├── __Lu_uYoC2RqSYF1Wv5GEchQ
│       │   ├── __NOunPv39SU-IcZHUPjfFSw
│       │   ├── __PhszAfkJRqyfCQme7HXTlw
│       │   ├── __PwGnRH2uRj-E8o5h18l4jA
│       │   ├── __T27uxGqgQoOj_0p86PDyuA
│       │   ├── __YN69iWCtRqqzjFJ9Bgk68w
│       │   ├── __Ywb0Yr67TH-S4tOHjnaoVQ
│       │   ├── __a0QKSHNlT8W-HouU8hVDUw
│       │   ├── __dXx6hK70Q8CuhvqtmOZx4Q
│       │   ├── __fbcWE1PUSXuYUjzvq0hcqA
│       │   ├── __ik7tosUWSSeHFq0Zli8vkA
│       │   ├── index-qXJ7Ux1WSH6w8jELeKiwPA
│       │   └── snap-DsseRXnjTwC_tcV0-VgAww.dat
│       └── meta-l2TmEHcBch0uJWW8rNjp.dat
├── meta-DsseRXnjTwC_tcV0-VgAww.dat
└── snap-DsseRXnjTwC_tcV0-VgAww.dat

3 directories, 23 files
```

File `index-4` contains the names of all the snapshots in the repository.

```
cat index-4 | jq
```

```json
{
  "snapshots": [
    {
      "name": "transactions.2021-01-10",
      "uuid": "DsseRXnjTwC_tcV0-VgAww",
      "state": 1,
      "index_metadata_lookup": {
        "toVoOSewT8eO7PbggT7SaA": "umyNu_9iRE65F5RYKcS21A-_na_-1-3-1"
      },
      "version": "7.10.1"
    }
  ],
  "indices": {
    "transactions": {
      "id": "toVoOSewT8eO7PbggT7SaA",
      "snapshots": [
        "DsseRXnjTwC_tcV0-VgAww"
      ],
      "shard_generations": [
        "qXJ7Ux1WSH6w8jELeKiwPA"
      ]
    }
  },
  "min_version": "7.9.0",
  "index_metadata_identifiers": {
    "umyNu_9iRE65F5RYKcS21A-_na_-1-3-1": "l2TmEHcBch0uJWW8rNjp"
  }
}
```

## References

- Konrad Beiske, "Snapshot And Restore", Elasticsearch, 2014.
  <https://www.elastic.co/fr/blog/found-elasticsearch-snapshot-and-restore>
