package com.github.jarrad.ldk;

import java.security.SecureRandom;

public class SecureRandomKeySeedProvider implements KeySeedProvider {

  private static final SecureRandom secureRandom;

  static {
    secureRandom = new SecureRandom();
  }

  @Override
  public byte[] get() {
    return secureRandom.generateSeed(32);
  }
}
