package io.mincong.dvf.demo;

import io.mincong.dvf.model.Transaction;
import io.mincong.dvf.service.TransactionBulkEsWriter;
import io.mincong.dvf.service.TransactionCsvReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class WritePathDemo {
  private static final Logger logger = LogManager.getLogger(WritePathDemo.class);

  private static final String CSV_PATH = "/Users/minconghuang/github/dvf/downloads/full.2020.csv";

  private static final String REPO_NAME = "dvf";

  /**
   * This the location of the backup.
   *
   * <p>It matches the location specified in the settings of PUT repository HTTP request. This value
   * is also specified in environment variable "path.repo". It is used for snapshot repository of
   * type "fs" (filesystem).
   *
   * @see <a
   *     href="https://www.elastic.co/guide/en/elasticsearch/reference/7.x/snapshots-register-repository.html">Register
   *     a snapshot repository</a>
   */
  private static final String BACKUP_LOCATION = REPO_NAME;

  private static final int BULK_SIZE = 1000;

  private static final int THREADS = Runtime.getRuntime().availableProcessors() * 2;

  private static final long INDEX_BULK_LIMIT = -1;

  public void run() {
    var builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
    logger.info("Start creating REST high-level client...");
    var executor = Executors.newFixedThreadPool(THREADS);
    try (var restClient = new RestHighLevelClient(builder)) {
      indexTransactions(restClient, executor).join();
      forceMerge(restClient);
      snapshot(restClient);
    } catch (IOException e) {
      logger.error("Failed to execute DVF program", e);
    } finally {
      executor.shutdown();
    }
  }

  public CompletableFuture<?> indexTransactions(RestHighLevelClient restClient, Executor executor) {
    var start = Instant.now();
    var csvReader = new TransactionCsvReader(BULK_SIZE);
    var esWriter =
        new TransactionBulkEsWriter(
            restClient, Transaction.INDEX_NAME, executor, WriteRequest.RefreshPolicy.NONE);
    //    var esWriter =
    //        new TransactionSimpleEsWriter(restClient, Transaction.INDEX_NAME, RefreshPolicy.NONE);

    var transactions = csvReader.readCsv(Path.of(CSV_PATH));
    if (INDEX_BULK_LIMIT > 0) {
      transactions = transactions.limit(INDEX_BULK_LIMIT);
    }

    esWriter.createIndex();
    logger.info("Start writing transaction...");
    return esWriter
        .write(transactions)
        .whenComplete(
            (ids, ex) -> {
              if (ex != null) {
                logger.error("Failed to complete", ex);
              } else {
                var duration = Duration.between(start, Instant.now());
                var speed = String.format("%.2f", ids.size() * 1.0 / duration.toSeconds());
                logger.info(
                    "Finished, indexed {} documents in {} (speed: {} docs/s)",
                    ids.size(),
                    duration,
                    speed);
              }
            });
  }

  public void forceMerge(RestHighLevelClient restClient) {
    logger.info("Start force merge");
    try {
      var request = new ForceMergeRequest(Transaction.INDEX_NAME).maxNumSegments(1);
      var response = restClient.indices().forcemerge(request, RequestOptions.DEFAULT);
      logger.info("Force merge response: {}", response);
    } catch (IOException e) {
      logger.error("Failed to perform force-merge for index " + Transaction.INDEX_NAME, e);
    }
  }

  /**
   * After snapshot repository creation:
   *
   * <pre>
   * curl -s localhost:9200/_snapshot/
   * </pre>
   *
   * <pre>
   * {
   *   "dvf": {
   *     "type": "fs",
   *     "settings": {
   *       "location": "dvf"
   *     }
   *   }
   * }
   * </pre>
   *
   * After snapshot creation:
   *
   * <pre>
   * curl -s localhost:9200/_snapshot/dvf/_all | jq
   * </pre>
   *
   * <pre>
   * {
   *   "snapshots": [
   *     {
   *       "snapshot": "transactions.2021-01-10",
   *       "uuid": "p-xx9kbWRKuKWaszJ9MJag",
   *       "version_id": 7100199,
   *       "version": "7.10.1",
   *       "indices": [
   *         "transactions"
   *       ],
   *       "data_streams": [],
   *       "include_global_state": true,
   *       "state": "SUCCESS",
   *       "start_time": "2021-01-09T21:28:17.854Z",
   *       "start_time_in_millis": 1610227697854,
   *       "end_time": "2021-01-09T21:28:18.455Z",
   *       "end_time_in_millis": 1610227698455,
   *       "duration_in_millis": 601,
   *       "failures": [],
   *       "shards": {
   *         "total": 1,
   *         "failed": 0,
   *         "successful": 1
   *       }
   *     }
   *   ]
   * }
   * </pre>
   *
   * @param restClient
   */
  public void snapshot(RestHighLevelClient restClient) {
    try {
      logger.info("Start creating snapshot repository");
      var putRepoRequest =
          new PutRepositoryRequest()
              .name(REPO_NAME)
              .type("fs")
              .settings(Map.of("location", BACKUP_LOCATION));
      var putRepoResponse =
          restClient.snapshot().createRepository(putRepoRequest, RequestOptions.DEFAULT);
      logger.info("Repository created: {}", putRepoResponse);
      logger.info("Start creating snapshot");
      var createSnapshotRequest =
          new CreateSnapshotRequest()
              .repository(REPO_NAME)
              .snapshot("transactions.2021-01-10")
              .waitForCompletion(true)
              .includeGlobalState(false);
      var createSnapshotResponse =
          restClient.snapshot().create(createSnapshotRequest, RequestOptions.DEFAULT);
      logger.info("Snapshot created: {}", createSnapshotResponse);

      var deleteIndexRequest = new DeleteIndexRequest().indices("transactions");
      var deletionIndexResponse =
          restClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      logger.info("Index deleted: {}", deletionIndexResponse);

      var restoreSnapshotRequest =
          new RestoreSnapshotRequest()
              .repository(REPO_NAME)
              .waitForCompletion(true)
              .snapshot("transactions.2021-01-10");
      var restoreSnapshotResponse =
          restClient.snapshot().restore(restoreSnapshotRequest, RequestOptions.DEFAULT);
      logger.info("Index restored: {}", restoreSnapshotResponse);
    } catch (IOException e) {
      logger.error("Failed to handle snapshot", e);
    }
  }
}
