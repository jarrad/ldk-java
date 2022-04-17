package com.github.jarrad.ldk;

import org.ldk.structs.ChannelMonitor;
import org.ldk.structs.ChannelMonitorUpdate;
import org.ldk.structs.MonitorUpdateId;
import org.ldk.structs.OutPoint;
import org.ldk.structs.Persist.PersistInterface;
import org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ;

import static java.util.Objects.requireNonNull;

public class LdkChannelPersist implements PersistInterface {

  private final LdkChannelRepo channelRepo;

  public LdkChannelPersist(final LdkChannelRepo channelRepo) {
    this.channelRepo = requireNonNull(channelRepo);
  }

  @Override
  public Result_NoneChannelMonitorUpdateErrZ persist_new_channel(
      OutPoint channel_id, ChannelMonitor data, MonitorUpdateId update_id) {
    final byte[] channelId = channel_id.to_channel_id();
    final byte[] state = data.write();

    // TODO
    // persist channel to repo

    return Result_NoneChannelMonitorUpdateErrZ.ok();
  }

  @Override
  public Result_NoneChannelMonitorUpdateErrZ update_persisted_channel(
      OutPoint channel_id,
      ChannelMonitorUpdate update,
      ChannelMonitor data,
      MonitorUpdateId update_id) {

    // TODO

    return Result_NoneChannelMonitorUpdateErrZ.ok();
  }
}
