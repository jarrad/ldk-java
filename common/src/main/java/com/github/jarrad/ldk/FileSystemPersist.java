package com.github.jarrad.ldk;

import org.ldk.structs.ChannelMonitor;
import org.ldk.structs.ChannelMonitorUpdate;
import org.ldk.structs.MonitorUpdateId;
import org.ldk.structs.OutPoint;
import org.ldk.structs.Persist.PersistInterface;
import org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ;

public class FileSystemPersist implements PersistInterface {

  @Override
  public Result_NoneChannelMonitorUpdateErrZ persist_new_channel(OutPoint channel_id,
      ChannelMonitor data, MonitorUpdateId update_id) {
    final byte[] channelData = data.write();
    // TODO

    return Result_NoneChannelMonitorUpdateErrZ.Result_NoneChannelMonitorUpdateErrZ_OK.ok();
  }

  @Override
  public Result_NoneChannelMonitorUpdateErrZ update_persisted_channel(OutPoint channel_id,
      ChannelMonitorUpdate update, ChannelMonitor data, MonitorUpdateId update_id) {
    final byte[] channelData = data.write();
    // TODO
    return Result_NoneChannelMonitorUpdateErrZ.Result_NoneChannelMonitorUpdateErrZ_OK.ok();
  }
}
