package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.ldk.batteries.NioPeerHandler;

public class PeerConnectionListener {

  private final NioPeerHandler peerHandler;

  private final int port;

  private final String ipV4Address;

  private final InetSocketAddress socketAddress;

  public PeerConnectionListener(final NioPeerHandler peerHandler, final int port,
      final String ipV4Address) {
    assert port >= 1 && port <= Math.pow(2, 16) : "invalid port";

    this.port = port;
    this.ipV4Address = ipV4Address;
    try {
      socketAddress = new InetSocketAddress(this.ipV4Address, this.port);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid ip provided", e);
    }

    this.peerHandler = requireNonNull(peerHandler);
  }

  public void bind() throws IOException {
    peerHandler.bind_listener(socketAddress);
  }

  public void interrupt() {
    peerHandler.interrupt();
  }
}
