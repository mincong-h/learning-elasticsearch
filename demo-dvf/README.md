# DVF

## Mappings

> Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [code_postal] in order to load field data by uninverting the inverted index. Note that this can use significant memory.]]; nested: ElasticsearchException[Elasticsearch exception [type=illegal_argument_exception, reason=Text fields are not optimised for operations that require per-document field data like aggregations and sorting, so these operations are disabled by default. Please use a keyword field instead. Alternatively, set fielddata=true on [code_postal] in order to load field data by uninverting the inverted index. Note that this can use significant memory.]];
## Search

Metric aggregations:
<https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html>

## Troubleshooting

### Out Of Memory

```
09:09:56.293 [I/O dispatcher 1] ERROR io.mincong.dvf.service.Main - Failed to complete
java.util.concurrent.CompletionException: java.lang.OutOfMemoryError: Java heap space
	at java.util.concurrent.CompletableFuture.encodeThrowable(CompletableFuture.java:314) ~[?:?]
	at java.util.concurrent.CompletableFuture.completeThrowable(CompletableFuture.java:319) ~[?:?]
	at java.util.concurrent.CompletableFuture$UniCompose.tryFire(CompletableFuture.java:1155) ~[?:?]
	at java.util.concurrent.CompletableFuture.postComplete(CompletableFuture.java:506) ~[?:?]
	at java.util.concurrent.CompletableFuture.complete(CompletableFuture.java:2137) ~[?:?]
	at org.elasticsearch.action.ActionListener$1.onResponse(ActionListener.java:63) [elasticsearch-7.8.0.jar:7.8.0]
	at org.elasticsearch.client.RestHighLevelClient$1.onSuccess(RestHighLevelClient.java:1760) [elasticsearch-rest-high-level-client-7.8.0.jar:7.8.0]
	at org.elasticsearch.client.RestClient$FailureTrackingResponseListener.onSuccess(RestClient.java:590) [elasticsearch-rest-client-7.8.0.jar:7.8.0]
	at org.elasticsearch.client.RestClient$1.completed(RestClient.java:333) [elasticsearch-rest-client-7.8.0.jar:7.8.0]
	at org.elasticsearch.client.RestClient$1.completed(RestClient.java:327) [elasticsearch-rest-client-7.8.0.jar:7.8.0]
	at org.apache.http.concurrent.BasicFuture.completed(BasicFuture.java:122) [httpcore-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.client.DefaultClientExchangeHandlerImpl.responseCompleted(DefaultClientExchangeHandlerImpl.java:181) [httpasyncclient-4.1.4.jar:4.1.4]
	at org.apache.http.nio.protocol.HttpAsyncRequestExecutor.processResponse(HttpAsyncRequestExecutor.java:448) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.nio.protocol.HttpAsyncRequestExecutor.inputReady(HttpAsyncRequestExecutor.java:338) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.DefaultNHttpClientConnection.consumeInput(DefaultNHttpClientConnection.java:265) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:81) [httpasyncclient-4.1.4.jar:4.1.4]
	at org.apache.http.impl.nio.client.InternalIODispatch.onInputReady(InternalIODispatch.java:39) [httpasyncclient-4.1.4.jar:4.1.4]
	at org.apache.http.impl.nio.reactor.AbstractIODispatch.inputReady(AbstractIODispatch.java:114) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.BaseIOReactor.readable(BaseIOReactor.java:162) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvent(AbstractIOReactor.java:337) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.processEvents(AbstractIOReactor.java:315) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.AbstractIOReactor.execute(AbstractIOReactor.java:276) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.BaseIOReactor.execute(BaseIOReactor.java:104) [httpcore-nio-4.4.12.jar:4.4.12]
	at org.apache.http.impl.nio.reactor.AbstractMultiworkerIOReactor$Worker.run(AbstractMultiworkerIOReactor.java:591) [httpcore-nio-4.4.12.jar:4.4.12]
	at java.lang.Thread.run(Thread.java:832) [?:?]
Caused by: java.lang.OutOfMemoryError: Java heap space
```

Solution:

1. Increase the heap size for the JVM: use minimum 4GB of memory `-Xms4g` and maximum `-Xmx4g` of memory for the Java Virtual Machine.
2. Optimize code

### Index Request Stuck

An index request was sent but there is no response.

```
09:58:24.650 [main] INFO  io.mincong.dvf.service.Main - Start creating REST high-level client...
09:58:27.668 [I/O dispatcher 1] INFO  io.mincong.dvf.service.TransactionEsWriter - Creation of index transactions is acknowledged
09:58:27.669 [I/O dispatcher 1] INFO  io.mincong.dvf.service.Main - Start writing transaction...
09:58:27.682 [I/O dispatcher 1] INFO  io.mincong.dvf.service.TransactionEsWriter - Indexing transaction Transaction{mutationId=2020-1, mutationDate=2020-01-07, dispositionNumber=000001, mutationNature=Vente, propertyValue=8000.0, addressNumber=, addressSuffix=, addressRoadName=FORTUNAT, addressRoadCode=B063, postalCode=01250, communeCode=01072, communeName=Ceyzériat, departmentCode=01, oldCommuneCode=, oldCommuneName=, plotId=01072000AK0216, oldPlotId=, volumeNumber=, numberLot1=, numberLot2=, numberLot3=, numberLot4=, numberLot5=, lotsCount=0, localTypeCode=, localType=, natureCultureCode=T, natureCulture=terres, specialNatureCultureCode=, specialNatureCulture=, landSurface=1061.0, location=Location{longitude=5.323522, latitude=46.171899}}
```

## References

- Base "Demande de valeurs foncières" (DVF)
  <https://cadastre.data.gouv.fr/dvf>
- Notice descriptive
  <https://www.data.gouv.fr/fr/datasets/r/d573456c-76eb-4276-b91c-e6b9c89d6656>
- Mapping types
  <https://www.elastic.co/guide/en/elasticsearch/reference/7.9/mapping-types.html>
