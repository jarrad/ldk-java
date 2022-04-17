package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import org.ldk.enums.Network;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.Filter;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.NetworkGraph;
import org.ldk.structs.Persist;
import org.ldk.structs.UserConfig;

/**
 * Container for all of the LDK structs.
 */
public class LdkContext {

  private final BroadcasterInterface broadcasterInterface;

  private final Persist persist;

  private final ChainMonitor chainMonitor;

  private final Network network;

  private final KeysManager keysManager;

  private final FeeEstimator feeEstimator;

  private final Logger logger;

  private final UserConfig userConfig;

  private final NetworkGraph networkGraph;

  private final Filter filter;

  public LdkContext(final BroadcasterInterface broadcasterInterface, final Persist persist,
      final ChainMonitor chainMonitor, final Network network, final KeysManager keysManager,
      final Filter filter, final NetworkGraph networkGraph, final FeeEstimator feeEstimator,
      final UserConfig userConfig, final Logger logger) {
    this.broadcasterInterface = requireNonNull(broadcasterInterface);
    this.persist = requireNonNull(persist);
    this.chainMonitor = requireNonNull(chainMonitor);
    this.network = requireNonNull(network);
    this.keysManager = requireNonNull(keysManager);
    this.filter = filter; // nullable
    this.networkGraph = networkGraph; // nullable
    this.feeEstimator = requireNonNull(feeEstimator);
    this.userConfig = requireNonNull(userConfig);
    this.logger = requireNonNull(logger);
  }

  public Filter getFilter() {
    return filter;
  }

  public NetworkGraph getNetworkGraph() {
    return networkGraph;
  }

  public UserConfig getUserConfig() {
    return userConfig;
  }

  public BroadcasterInterface getBroadcasterInterface() {
    return broadcasterInterface;
  }

  public Persist getPersist() {
    return persist;
  }

  public ChainMonitor getChainMonitor() {
    return chainMonitor;
  }

  public Network getNetwork() {
    return network;
  }

  public KeysManager getKeysManager() {
    return keysManager;
  }

  public FeeEstimator getFeeEstimator() {
    return feeEstimator;
  }

  public Logger getLogger() {
    return logger;
  }
}
