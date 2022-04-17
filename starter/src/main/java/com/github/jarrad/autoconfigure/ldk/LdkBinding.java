package com.github.jarrad.autoconfigure.ldk;

import org.ldk.enums.*;
import org.ldk.impl.bindings;
import org.springframework.beans.factory.InitializingBean;

/**
 * Force loading of the native LDK bindings.
 */
public class LdkBinding implements InitializingBean {

  LdkBinding() {
    bindings.new_empty_slice_vec();
  }

  @Override
  public void afterPropertiesSet() throws Exception {

  }

  /**
   * Convert the given input value into the corresponding network.
   * <p>
   *     eg. "mainnet" -> LDKNetwork_Bitcoin
   * </p>
   */
  public static org.ldk.enums.Network resolve(final String value) {
    switch (value) {
      case "mainnet":
        return Network.LDKNetwork_Bitcoin;
      case "signet":
        return Network.LDKNetwork_Signet;
      case "testnet":
        return Network.LDKNetwork_Testnet;
      default:
        return Network.LDKNetwork_Regtest;
    }
  }

}
