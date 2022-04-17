package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import com.github.jarrad.ldk.BlockchainClient.Block;
import com.github.jarrad.ldk.LdkChannel.LdkChannelStatus;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import org.bouncycastle.util.encoders.Hex;
import org.ldk.batteries.ChannelManagerConstructor;
import org.ldk.batteries.ChannelManagerConstructor.EventHandler;
import org.ldk.batteries.ChannelManagerConstructor.InvalidSerializedDataException;
import org.ldk.structs.ChannelManager;

/**
 * Manager which keeps track of a number of channels and sends messages to the appropriate channel,
 * also tracking HTLC preimages and forwarding onion packets appropriately.
 *
 * <p>Implements ChannelMessageHandler, handling the multi-channel parts and passing things through
 * to individual Channels.
 *
 * <p>Implements Writeable to write out all channel state to disk. Implies peer_disconnected() for
 * all peers during write/read (though does not modify this instance, only the instance being
 * serialized). This will result in any channels which have not yet exchanged funding_created (ie
 * called funding_transaction_generated for outbound channels).
 *
 * <p>Note that you can be a bit lazier about writing out ChannelManager than you can be with
 * ChannelMonitors. With ChannelMonitors you MUST write each monitor update out to disk before
 * returning from chain::Watch::watch_/update_channel, with ChannelManagers, writing updates happens
 * out-of-band (and will prevent any other ChannelManager operations from occurring during the
 * serialization process). If the deserialized version is out-of-date compared to the
 * ChannelMonitors passed by reference to read(), those channels will be force-closed based on the
 * ChannelMonitor state and no funds will be lost (mod on-chain transaction fees).
 *
 * <p>Note that the deserializer is only implemented for (BlockHash, ChannelManager), which tells
 * you the last block hash which was block_connect()ed. You MUST rescan any blocks along the “reorg
 * path” (ie call block_disconnected() until you get to a common block and then call
 * block_connected() to step towards your best block) upon deserialization before using the object!
 *
 * <p>Note that ChannelManager is responsible for tracking liveness of its channels and generating
 * ChannelUpdate messages informing peers that the channel is temporarily disabled. To avoid spam
 * due to quick disconnection/reconnection, updates are not sent until the channel has been offline
 * for a full minute. In order to track this, you must call timer_tick_occurred roughly once per
 * minute, though it doesn’t have to be perfect.
 *
 * <p>Rather than using a plain ChannelManager, it is preferable to use either a
 * SimpleArcChannelManager a SimpleRefChannelManager, for conciseness. See their documentation for
 * more details, but essentially you should default to using a SimpleRefChannelManager, and use a
 * SimpleArcChannelManager when you require a ChannelManager with a static lifetime, such as when
 * you’re using lightning-net-tokio.
 */
public class LdkChannelManager {

  private final BlockchainClient blockchainClient;

  private final LdkChannelRepo ldkChannelRepo;

  private final LdkContext ldk;

  private final AtomicReference<ChannelManager> channelManager;

  private final EventHandler eventHandler;

  public LdkChannelManager(
      final BlockchainClient blockchainClient,
      final LdkChannelRepo ldkChannelRepo,
      final LdkContext ldk,
      final EventHandler eventHandler) {
    this.blockchainClient = requireNonNull(blockchainClient);
    this.ldkChannelRepo = requireNonNull(ldkChannelRepo);
    this.ldk = requireNonNull(ldk);
    this.eventHandler = requireNonNull(eventHandler);
    channelManager = new AtomicReference<>();
  }

  private ChannelManagerConstructor restoreChannels() {
    // find channel states and serialize to byte array
    final Collection<LdkChannel> channels = ldkChannelRepo.findAll();
    final byte[][] monitors =
        channels.stream().map(LdkChannel::getChannelState).toArray(byte[][]::new);

    // read the manage state
    final byte[] manager = new byte[0]; // FIXME

    final ChannelManagerConstructor constructor;
    try {
      constructor =
          new ChannelManagerConstructor(
              manager,
              monitors,
              ldk.getUserConfig(),
              ldk.getKeysManager().as_KeysInterface(),
              ldk.getFeeEstimator(),
              ldk.getChainMonitor(),
              ldk.getFilter(),
              ldk.getNetworkGraph(),
              ldk.getBroadcasterInterface(),
              ldk.getLogger());
    } catch (InvalidSerializedDataException e) {
      // FIXME
      throw new RuntimeException(e);
    }
    return constructor;
  }

  private ChannelManagerConstructor load() {
    final Block blockTip = blockchainClient.getBlockTip();
    final byte[] blockHash = Hex.decode(blockTip.getHash());
    final ChannelManagerConstructor constructor =
        new ChannelManagerConstructor(
            ldk.getNetwork(),
            ldk.getUserConfig(),
            blockHash,
            blockTip.getHeight(),
            ldk.getKeysManager().as_KeysInterface(),
            ldk.getFeeEstimator(),
            ldk.getChainMonitor(),
            ldk.getNetworkGraph(),
            ldk.getBroadcasterInterface(),
            ldk.getLogger());
    return constructor;
  }

  public void start() {
    if (channelManager.get() != null) {
      // already initialized
      return;
    }

    final ChannelManagerConstructor constructor;
    if (ldkChannelRepo.countByStatus(LdkChannelStatus.OPEN) < 1) {
      // no open channels to restore
      constructor = load();
    } else {
      // restore existing channels
      constructor = restoreChannels();
    }
    syncChain(constructor);
  }

  protected void syncChain(final ChannelManagerConstructor constructor) {
    // TODO

    final ChannelManager _channelManager = constructor.channel_manager;
    channelManager.compareAndSet(null, _channelManager);
    constructor.chain_sync_completed(eventHandler, null); // TODO scoring?
  }

  public void stop() {
    if (channelManager.get() == null) {
      // already stopped
      return;
    }
    // TODO
  }
}
