package com.github.jarrad.ldk;

import com.github.jarrad.ldk.LdkChannel.LdkChannelStatus;
import java.util.Collection;

public interface LdkChannelRepo {

  void save(final LdkChannel channel);

  int countByStatus(final LdkChannelStatus status);

  Collection<LdkChannel> findAll();

  Collection<LdkChannel> findByStatus(final LdkChannelStatus status);
}
