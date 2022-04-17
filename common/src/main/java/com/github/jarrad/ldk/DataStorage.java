package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

public interface DataStorage {

  void persist(final Data data) throws IOException;

  Data load(final String key);

  class Data {

    public static Data of(final String key, final byte[] value) {
      return new Data(key, value);
    }

    private Data(final String key, final byte[] value) {
      assert key != null && key.trim().length() > 0;
      this.key = key;
      this.value = requireNonNull(value);
    }

    private String key;

    private byte[] value;

    public String getKey() {
      return key;
    }

    public byte[] getValue() {
      return value;
    }
  }

}
