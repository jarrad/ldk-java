package com.github.jarrad.ldk;

public interface BlockchainClient {

  Block getBlockTip();

  interface Block {

    int getHeight();

    String getHash();
  }
}
