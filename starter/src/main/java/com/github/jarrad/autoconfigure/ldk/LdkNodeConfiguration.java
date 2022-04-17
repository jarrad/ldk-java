package com.github.jarrad.autoconfigure.ldk;

import com.github.jarrad.ldk.BlockchainClient;
import com.github.jarrad.ldk.ChannelManagerEventHandler;
import com.github.jarrad.ldk.DataStorage;
import com.github.jarrad.ldk.DataStorage.Data;
import com.github.jarrad.ldk.DataStoragePersist;
import com.github.jarrad.ldk.DefaultEventHandler;
import com.github.jarrad.ldk.FileSystemChannelRepo;
import com.github.jarrad.ldk.FileSystemDataStorage;
import com.github.jarrad.ldk.InMemoryCachingFeeEstimator;
import com.github.jarrad.ldk.KeySeedProvider;
import com.github.jarrad.ldk.LdkChannelManager;
import com.github.jarrad.ldk.LdkChannelRepo;
import com.github.jarrad.ldk.LdkContext;
import com.github.jarrad.ldk.SecureRandomKeySeedProvider;
import com.github.jarrad.ldk.Slf4jLogger;
import org.ldk.batteries.ChannelManagerConstructor.EventHandler;
import org.ldk.enums.Network;
import org.ldk.structs.BestBlock;
import org.ldk.structs.BroadcasterInterface;
import org.ldk.structs.BroadcasterInterface.BroadcasterInterfaceInterface;
import org.ldk.structs.ChainMonitor;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.FeeEstimator.FeeEstimatorInterface;
import org.ldk.structs.Filter;
import org.ldk.structs.Filter.FilterInterface;
import org.ldk.structs.KeysManager;
import org.ldk.structs.Logger;
import org.ldk.structs.Logger.LoggerInterface;
import org.ldk.structs.NetworkGraph;
import org.ldk.structs.Option_FilterZ.Some;
import org.ldk.structs.Persist;
import org.ldk.structs.Persist.PersistInterface;
import org.ldk.structs.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Configuration for setting up the necessary structs for running a lightning node.
 *
 * @see <a href="https://lightningdevkit.org/tutorials/build_a_node_in_java/">Build a node in
 *     Java</a>
 */
@Configuration(proxyBeanMethods = false)
public class LdkNodeConfiguration {

  /** Forces loading of the native ldk bindings. */
  @Order(Integer.MIN_VALUE)
  @Bean
  LdkBinding ldkBinding() {
    return new LdkBinding();
  }

  /**
   * Return the bean used for estimating fees for on-chain transactions that LDK wants to broadcast.
   *
   * <ul>
   *   <li>Fees must be returned in: satoshis per 1000 weight units
   *   <li>Fees returned must be no smaller than 253 (equivalent to 1 satoshi/vbyte, rounded up)
   *   <li>To reduce network traffic, you may want to cache fee results rather than retrieving fresh
   *       ones every time
   * </ul>
   */
  @Bean
  FeeEstimator feeEstimator(final FeeEstimatorInterface feeEstimatorInterface) {
    return FeeEstimator.new_impl(feeEstimatorInterface);
  }

  @Bean
  Logger logger(final LoggerInterface loggerInterface) {
    return Logger.new_impl(loggerInterface);
  }

  /**
   * Return the bean used for broadcasting various Lightning transactions to the bitcoin network.
   */
  @Bean
  BroadcasterInterface broadcasterInterface(
      final BroadcasterInterfaceInterface broadcasterInterfaceInterface) {
    return BroadcasterInterface.new_impl(broadcasterInterfaceInterface);
  }

  /**
   * Return the bean used for persisting crucial channel data in a timely manner.
   *
   * <p>ChannelMonitors are objects which are capable of responding to on-chain events for a given
   * channel. Thus, you will have one ChannelMonitor per channel, identified by the funding output
   * id, above. They are persisted in real-time and the Persist methods will block progress on
   * sending or receiving payments until they return. You must ensure that ChannelMonitors are
   * durably persisted to disk before returning or you may lose funds.
   */
  @Bean
  Persist persist(final PersistInterface persistInterface) {
    return Persist.new_impl(persistInterface);
  }

  /**
   * Return the bean used for monitoring the chain for lighting transactions that are relevant to
   * our node, and broadcasting transactions if need be.
   */
  @Bean
  ChainMonitor chainMonitor(
      @Autowired(required = false) final Filter filter,
      final BroadcasterInterface broadcasterInterface,
      final Logger logger,
      final FeeEstimator feeEstimator,
      final Persist persist) {
    return ChainMonitor.of(
        filter == null ? Some.none() : Some.some(filter),
        broadcasterInterface,
        logger,
        feeEstimator,
        persist);
  }

  /**
   * Return the bean used for providing keys for signing Lightning transactions.
   *
   * @see <a href="https://lightningdevkit.org/key_management/">Key Management guide</a>
   */
  @Bean
  KeysManager keysManager(final KeySeedProvider keySeedProvider) {
    final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    final long nowSeconds = now.toEpochSecond();
    final byte[] keySeed = keySeedProvider.get();
    return KeysManager.of(keySeed, nowSeconds, now.getNano());
  }

  @Bean(initMethod = "start", destroyMethod = "stop")
  LdkChannelManager ldkChannelManager(
      final BlockchainClient blockchainClient,
      final LdkChannelRepo ldkChannelRepo,
      final LdkContext ldk,
      final EventHandler eventHandler) {
    return new LdkChannelManager(blockchainClient, ldkChannelRepo, ldk, eventHandler);
  }

  @Bean
  LdkContext ldkContext(
      final BroadcasterInterface broadcasterInterface,
      final Persist persist,
      final ChainMonitor chainMonitor,
      final LdkNodeProperties nodeProperties,
      final KeysManager keysManager,
      @Autowired(required = false) final Filter filter,
      final NetworkGraph networkGraph,
      final FeeEstimator feeEstimator,
      final UserConfig userConfig,
      final Logger logger) {
    final Network network = LdkBinding.resolve(nodeProperties.getNetwork());
    return new LdkContext(
        broadcasterInterface,
        persist,
        chainMonitor,
        network,
        keysManager,
        filter,
        networkGraph,
        feeEstimator,
        userConfig,
        logger);
  }

  @Bean
  DefaultEventHandler defaultEventHandler(
      final Collection<ChannelManagerEventHandler<?>> handlers, final DataStorage dataStorage) {
    return new DefaultEventHandler(handlers, dataStorage);
  }

  /**
   * By default, we will load the network graph in order to generate routes to send payments over.
   *
   * <p>Note, this struct is not required if you are providing your own routes and can be disabled
   * by setting the property <code>ldk.node.routes.enabled</code> to <code>false</code>.
   */
  @ConditionalOnProperty(
      value = "ldk.node.routes.enabled",
      havingValue = "true",
      matchIfMissing = true)
  @Configuration
  static class RouteConfig {

    /**
     * Optional dependency: BestBlock, the best known block as identified by its hash and height.
     * Recommended to retrieve the genesis block from the target network.
     */
    @Bean
    NetworkGraph networkGraph(final LdkBinding ldkBinding) {
      final BestBlock genesisBlock = BestBlock.from_genesis(Network.LDKNetwork_Bitcoin);
      return NetworkGraph.of(genesisBlock.block_hash());
    }
  }

  /**
   * if you are not providing full blocks, LDK uses this object to tell you what transactions and
   * outputs to watch for on-chain.
   *
   * @see <a href="https://lightningdevkit.org/blockchain_data/introduction/">Blockchain Data
   *     Guide</a>
   */
  @ConditionalOnProperty(value = "ldk.node.filter.enabled", havingValue = "true")
  @Configuration
  static class FilterConfig {

    @Bean
    Filter filter(final FilterInterface filterInterface) {
      return Filter.new_impl(filterInterface);
    }
  }

  @ConditionalOnClass(org.slf4j.LoggerFactory.class)
  @Configuration
  static class Slf4jConfig {

    @Bean
    Slf4jLogger slf4jLogger() {
      return new Slf4jLogger();
    }
  }

  @ConditionalOnProperty(
      value = "ldk.node.fees.caching.enabled",
      havingValue = "true",
      matchIfMissing = true)
  @Configuration
  static class FeeCachingConfig {

    @Primary
    @Bean
    InMemoryCachingFeeEstimator inMemoryCachingFeeEstimator(
        final FeeEstimatorInterface feeEstimatorInterface) {
      return new InMemoryCachingFeeEstimator(feeEstimatorInterface);
    }
  }

  @Configuration
  static class DefaultConfig {

    @ConditionalOnMissingBean(KeySeedProvider.class)
    @Bean
    SecureRandomKeySeedProvider secureRandomKeySeedProvider() {
      return new SecureRandomKeySeedProvider();
    }

    @ConditionalOnMissingBean(PersistInterface.class)
    @Bean
    DataStoragePersist dataStoragePersist(final DataStorage dataStorage) {
      return new DataStoragePersist(dataStorage);
    }

    @ConditionalOnMissingBean(DataStorage.class)
    @Bean
    FileSystemDataStorage fileSystemDataStorage() {
      final String basedirName = System.getProperty("user.dir");
      final File basedir = new File(basedirName);
      assert basedir.exists() && basedir.isDirectory() && basedir.canWrite()
          : "cannot access " + basedirName;
      return new FileSystemDataStorage(basedir);
    }

    @ConditionalOnMissingBean(LdkChannelRepo.class)
    @Bean
    FileSystemChannelRepo fileSystemChannelRepo(final FileSystemDataStorage dataStorage) {
      return new FileSystemChannelRepo(dataStorage);
    }

    @ConditionalOnMissingBean
    @Bean
    UserConfig userConfig() {
      return UserConfig.with_default();
    }
  }
}
