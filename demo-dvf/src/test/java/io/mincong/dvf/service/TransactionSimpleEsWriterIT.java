package io.mincong.dvf.service;

import io.mincong.dvf.model.Transaction;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;

public class TransactionSimpleEsWriterIT extends TransactionEsWriterAbstractIT {

  @Override
  protected EsWriter newEsWriter() {
    return new TransactionSimpleEsWriter(
        restClient, Transaction.indexNameForYear(year), RefreshPolicy.IMMEDIATE);
  }
}
