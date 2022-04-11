package com.github.jarrad.ldk;

import org.ldk.impl.bindings.LDKEvent.FundingGenerationReady;
import org.ldk.impl.bindings.LDKEvent.PaymentReceived;
import org.ldk.structs.Event.ChannelClosed;
import org.ldk.structs.Event.PaymentForwarded;
import org.ldk.structs.Event.PaymentPathFailed;
import org.ldk.structs.Event.PaymentSent;
import org.ldk.structs.Event.PendingHTLCsForwardable;
import org.ldk.structs.Event.SpendableOutputs;

public interface LightningEventHandler {

  void process(final FundingGenerationReady event);

  void process(final PaymentReceived event);

  void process(final PaymentSent event);

  void process(final PaymentPathFailed event);

  void process(final PendingHTLCsForwardable event);

  void process(final SpendableOutputs event);

  void process(final PaymentForwarded event);

  void process(final ChannelClosed event);

  interface LightningEvent {

    void accept(final LightningEventHandler handler);
  }

}
