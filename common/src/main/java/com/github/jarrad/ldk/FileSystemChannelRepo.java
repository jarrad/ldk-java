package com.github.jarrad.ldk;

import com.github.jarrad.ldk.DataStorage.Data;
import com.github.jarrad.ldk.LdkChannel.LdkChannelStatus;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class FileSystemChannelRepo implements LdkChannelRepo {

  private final FileSystemDataStorage dataStorage;

  public FileSystemChannelRepo(final FileSystemDataStorage dataStorage) {
    this.dataStorage = requireNonNull(dataStorage);
  }

  @Override
  public void save(final LdkChannel channel) {
    final String path = String.format("channels/%s", channel.getChannelId());
    final byte[] channelState = channel.getChannelState();
    final Data data = Data.of(path, channelState);
    try {
      dataStorage.persist(data);
    } catch (IOException e) {
      // FIXME
      throw new RuntimeException(e);
    }
  }

  @Override
  public int countByStatus(final LdkChannelStatus status) {
    return 0;
  }

  @Override
  public Collection<LdkChannel> findAll() {
    return Collections.emptyList();
  }

  @Override
  public Collection<LdkChannel> findByStatus(final LdkChannelStatus status) {
    return Collections.emptyList();
  }
}
