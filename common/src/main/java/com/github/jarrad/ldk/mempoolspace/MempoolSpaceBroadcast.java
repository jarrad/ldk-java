package com.github.jarrad.ldk.mempoolspace;

import static java.util.Objects.requireNonNull;

import org.github.jarrad.mempoolspace.MempoolSpaceClient;
import org.ldk.structs.BroadcasterInterface.BroadcasterInterfaceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** BroadcasterInterface to will publish a bitcoin transaction to the <em>mempool.space</em> API. */
public class MempoolSpaceBroadcast implements BroadcasterInterfaceInterface {

  private static final Logger logger = LoggerFactory.getLogger(MempoolSpaceBroadcast.class);

  private final MempoolSpaceClient mempoolSpaceClient;

  public MempoolSpaceBroadcast(final MempoolSpaceClient mempoolSpaceClient) {
    this.mempoolSpaceClient = requireNonNull(mempoolSpaceClient);
  }

  @Override
  public void broadcast_transaction(final byte[] tx) {
    final String txid = mempoolSpaceClient.broadcast(tx);
    logger.info("Broadcast as {}", txid);
  }
}
