package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

public interface DataStorage {

  void persist(final Data data) throws IOException;

  class Data {

    public static Data of(final String name, final byte[] value) {
      return new Data(name, value);
    }

    private Data(final String name, final byte[] value) {
      assert name != null && name.trim().length() > 0;
      this.name = name;
      this.value = requireNonNull(value);
    }

    private String name;

    private byte[] value;

    public String getName() {
      return name;
    }

    public byte[] getValue() {
      return value;
    }
  }

}
