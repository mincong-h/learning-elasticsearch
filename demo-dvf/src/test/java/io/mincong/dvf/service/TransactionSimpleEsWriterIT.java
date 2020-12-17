package io.mincong.dvf.service;

import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;

public class TransactionSimpleEsWriterIT extends TransactionEsWriterAbstractIT {

  @Override
  protected EsWriter newEsWriter() {
    return new TransactionSimpleEsWriter(restClient, RefreshPolicy.IMMEDIATE);
  }
}
