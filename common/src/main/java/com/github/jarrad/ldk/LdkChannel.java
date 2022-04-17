package com.github.jarrad.ldk;

import java.util.Arrays;
import java.util.Objects;

public class LdkChannel {

  enum LdkChannelStatus {

    UNKNOWN,
    PENDING,
    OPEN,
    CLOSED;

  }

  private String channelId;

  private byte[] channelState;

  private LdkChannelStatus status;

  public String getChannelId() {
    return channelId;
  }

  public LdkChannelStatus getStatus() {
    return status;
  }

  public byte[] getChannelState() {
    return channelState;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LdkChannel that = (LdkChannel) o;
    return Objects.equals(channelId, that.channelId) && Arrays.equals(channelState, that.channelState) && status == that.status;
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(channelId, status);
    result = 31 * result + Arrays.hashCode(channelState);
    return result;
  }

  @Override
  public String toString() {
    return "LdkChannel{" +
            "channelId='" + channelId + '\'' +
            ", status=" + status +
            '}';
  }
}
