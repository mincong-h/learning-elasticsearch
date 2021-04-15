package io.mincong.dvf.service;

import io.mincong.dvf.model.Transaction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.junit.After;
import org.junit.Before;

public class TransactionBulkEsWriterIT extends TransactionEsWriterAbstractIT {

  private ExecutorService executor;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    executor = Executors.newSingleThreadExecutor();
  }

  @Override
  @After
  public void tearDown() throws Exception {
    executor.shutdownNow();
    super.tearDown();
  }

  @Override
  protected EsWriter newEsWriter() {
    return new TransactionBulkEsWriter(
        restClient, Transaction.indexNameForYear(year), executor, RefreshPolicy.IMMEDIATE);
  }
}
