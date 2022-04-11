package com.github.jarrad.autoconfigure.ldk;

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
}
