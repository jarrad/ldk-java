package com.github.jarrad.ldk;

public class LdkChannel {

  enum LdkChannelStatus {

    UNKNOWN,
    PENDING,
    OPEN,
    CLOSED;

  }

  private byte[] channelState;

  private LdkChannelStatus status;

  public LdkChannelStatus getStatus() {
    return status;
  }

  public byte[] getChannelState() {
    return channelState;
  }
}
