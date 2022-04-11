package com.github.jarrad.ldk;

import java.util.function.BiConsumer;
import org.ldk.structs.Logger;
import org.ldk.structs.Record;
import org.slf4j.LoggerFactory;

public class Slf4jLogger implements Logger.LoggerInterface {

  @Override
  public void log(final Record record) {
    final String loggerName = String.format("%s/%s", record.get_module_path(), record.get_file());

    final org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);

    final BiConsumer<String, Object[]> log;

    switch (record.get_level()) {
      case LDKLevel_Trace:
        log = logger::trace;
        break;
      case LDKLevel_Debug:
        log = logger::debug;
        break;
      case LDKLevel_Warn:
        log = logger::warn;
        break;
      case LDKLevel_Error:
        log = logger::error;
        break;
      case LDKLevel_Info:
      case LDKLevel_Gossip:
      default:
        log = logger::info;
        break;
    }
    log.accept("line {}: {}", new Object[] { record.get_line(), record.get_args() });
  }

}
