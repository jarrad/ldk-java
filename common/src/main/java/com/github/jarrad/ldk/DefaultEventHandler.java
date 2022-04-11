package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import com.github.jarrad.ldk.DataStorage.Data;
import java.io.IOException;
import java.util.Collection;
import org.ldk.batteries.ChannelManagerConstructor.EventHandler;
import org.ldk.structs.Event;

public class DefaultEventHandler implements EventHandler {

  private final Collection<ChannelManagerEventHandler<?>> eventHandlers;

  private final DataStorage storage;

  public DefaultEventHandler(final Collection<ChannelManagerEventHandler<?>> eventHandlers, final DataStorage storage) {
    this.eventHandlers = requireNonNull(eventHandlers);
    this.storage = requireNonNull(storage);
  }

  @Override
  public void handle_event(final Event event) {
    eventHandlers.stream()
        .filter(handler -> handler.isAcceptable(event))
        .forEach(handler -> handler.process(event));
  }

  @Override
  public void persist_manager(byte[] channel_manager_bytes) {
    try {
      storage.persist(Data.of("channel_manager", channel_manager_bytes));
    } catch (IOException e) {
      // FIXME
    }
  }
}
