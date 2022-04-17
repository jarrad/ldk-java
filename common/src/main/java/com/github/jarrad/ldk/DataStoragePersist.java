package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import com.github.jarrad.ldk.DataStorage.Data;
import java.io.IOException;
import java.util.Base64;
import org.ldk.enums.ChannelMonitorUpdateErr;
import org.ldk.structs.ChannelMonitor;
import org.ldk.structs.ChannelMonitorUpdate;
import org.ldk.structs.MonitorUpdateId;
import org.ldk.structs.OutPoint;
import org.ldk.structs.Persist.PersistInterface;
import org.ldk.structs.Result_NoneChannelMonitorUpdateErrZ;

public class DataStoragePersist implements PersistInterface {

  private final DataStorage dataStorage;

  public DataStoragePersist(final DataStorage dataStorage) {
    this.dataStorage = requireNonNull(dataStorage);
  }

  @Override
  public Result_NoneChannelMonitorUpdateErrZ persist_new_channel(OutPoint channel_id,
      ChannelMonitor data, MonitorUpdateId update_id) {
    return write(channel_id, data);
  }

  @Override
  public Result_NoneChannelMonitorUpdateErrZ update_persisted_channel(OutPoint channel_id,
      ChannelMonitorUpdate update, ChannelMonitor data, MonitorUpdateId update_id) {
    return write(channel_id, data);
  }

  private static String getName(final OutPoint channelId) {
    final String id = Base64.getEncoder().withoutPadding().encodeToString(channelId.to_channel_id());
    return String.format("channels/%s", id);
  }

  private Result_NoneChannelMonitorUpdateErrZ write(final OutPoint channelId, ChannelMonitor data) {
    final byte[] channelData = data.write();
    try {
      dataStorage.persist(Data.of(getName(channelId), channelData));
      return Result_NoneChannelMonitorUpdateErrZ.Result_NoneChannelMonitorUpdateErrZ_OK.ok();
    } catch (IOException e) {
      // FIXME
      return Result_NoneChannelMonitorUpdateErrZ.Result_NoneChannelMonitorUpdateErrZ_Err.err(
          ChannelMonitorUpdateErr.LDKChannelMonitorUpdateErr_PermanentFailure);
    }
  }

}
