package com.github.jarrad.ldk;

import java.util.function.Consumer;
import org.ldk.structs.Event;

public interface ChannelManagerEventHandler<T extends Event> extends Consumer<T> {

  default boolean isAcceptable(final Event event) {
    return event != null && getEventType().isInstance(event);
  }

  /**
   * Return the type of event that is processable by this handler.
   */
  Class<T> getEventType();

  @SuppressWarnings("unchecked")
  default void process(final Event e) {
    if (!isAcceptable(e)) {
      throw new IllegalArgumentException("invalid event type");
    }
    accept((T) e);
  }

}
