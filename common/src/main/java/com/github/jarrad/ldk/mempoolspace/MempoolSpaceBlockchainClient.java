package com.github.jarrad.ldk.mempoolspace;

import com.github.jarrad.ldk.BlockchainClient;
import org.github.jarrad.mempoolspace.MempoolSpaceClient;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class MempoolSpaceBlockchainClient implements BlockchainClient {

  private final MempoolSpaceClient client;

  public MempoolSpaceBlockchainClient(final MempoolSpaceClient client) {
    this.client = requireNonNull(client);
  }

  @Override
  public Block getBlockTip() {
    final int height = client.getBlockTipHeight();
    final String hash = client.getBlockTipHash();
    return BlockImpl.of(height, hash);
  }

  static class BlockImpl implements Block {

    static Block of(final int height, final String hash) {
      return new BlockImpl(height, hash);
    }

    private final int height;

    private final String hash;

    private BlockImpl(int height, String hash) {
      assert height > 0;
      this.height = height;
      this.hash = requireNonNull(hash);
    }

    @Override
    public int getHeight() {
      return height;
    }

    @Override
    public String getHash() {
      return hash;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      BlockImpl block = (BlockImpl) o;
      return height == block.height && Objects.equals(hash, block.hash);
    }

    @Override
    public int hashCode() {
      return Objects.hash(height, hash);
    }

    @Override
    public String toString() {
      return "BlockImpl{" +
              "height=" + height +
              ", hash='" + hash + '\'' +
              '}';
    }
  }
}
