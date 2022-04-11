package com.github.jarrad.autoconfigure.ldk;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("ldk.node")
public class LdkNodeProperties {

  /**
   * Base64 encoded keySeed
   */
  private String keySeed;

  private Bind bind = new Bind();

  private Fees fees = new Fees();

  public Fees getFees() {
    return fees;
  }

  public void setFees(Fees fees) {
    this.fees = fees;
  }

  public Bind getBind() {
    return bind;
  }

  public void setBind(
      Bind bind) {
    this.bind = bind;
  }

  public String getKeySeed() {
    return keySeed;
  }

  public void setKeySeed(String keySeed) {
    this.keySeed = keySeed;
  }

  public static class Bind {

    private int port = 9735;

    private String ipV4Address = "0.0.0.0";

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getIpV4Address() {
      return ipV4Address;
    }

    public void setIpV4Address(String ipV4Address) {
      this.ipV4Address = ipV4Address;
    }
  }

  public static class Fees {

    private Caching caching = new Caching();

    public Caching getCaching() {
      return caching;
    }

    public void setCaching(final Caching caching) {
      this.caching = caching;
    }

    public static class Caching {

      private boolean enabled;

      public boolean isEnabled() {
        return enabled;
      }

      public void setEnabled(boolean enabled) {
        this.enabled = enabled;
      }
    }

  }
}
