package com.github.jarrad.autoconfigure.ldk;

import com.github.jarrad.autoconfigure.ldk.MempoolSpaceConfiguration.MempoolProperties;
import com.github.jarrad.ldk.mempoolspace.MempoolSpaceBlockchainClient;
import com.github.jarrad.ldk.mempoolspace.MempoolSpaceBroadcast;
import com.github.jarrad.ldk.mempoolspace.MempoolSpaceFeeEstimator;
import org.github.jarrad.mempoolspace.MempoolSpaceClient;
import org.github.jarrad.mempoolspace.feign.FeignMempoolSpaceClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({MempoolProperties.class})
@ConditionalOnClass(FeignMempoolSpaceClient.class)
@Configuration
public class MempoolSpaceConfiguration {

  @Bean
  FeignMempoolSpaceClient feignMempoolSpaceClient() {
    return new FeignMempoolSpaceClient();
  }

  @Bean
  MempoolSpaceFeeEstimator mempoolSpaceFeeEstimator(final MempoolSpaceClient mempoolSpaceClient) {
    return new MempoolSpaceFeeEstimator(mempoolSpaceClient);
  }

  @ConditionalOnProperty(value = "ldk.node.mempool.broadcast.enabled", havingValue = "true")
  @Bean
  MempoolSpaceBroadcast mempoolSpaceBroadcast(final MempoolSpaceClient mempoolSpaceClient) {
    return new MempoolSpaceBroadcast(mempoolSpaceClient);
  }

  @Bean
  MempoolSpaceBlockchainClient mempoolSpaceBlockchainClient(
      final MempoolSpaceClient mempoolSpaceClient) {
    return new MempoolSpaceBlockchainClient(mempoolSpaceClient);
  }

  @ConfigurationProperties("ldk.node.mempool")
  static class MempoolProperties {

    private Broadcast broadcast = new Broadcast();

    public Broadcast getBroadcast() {
      return broadcast;
    }

    public void setBroadcast(Broadcast broadcast) {
      this.broadcast = broadcast;
    }

    static class Broadcast {

      private boolean enabled = false;

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }
    }
  }
}
