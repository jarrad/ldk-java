package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileSystemDataStorage implements DataStorage {

  private final File basedir;

  public FileSystemDataStorage(final File basedir) {
    this.basedir = requireNonNull(basedir);
  }

  @Override
  public Data load(final String key) {
    return null;
  }

  @Override
  public void persist(final Data data) throws IOException {

    final String filename = String.format("%s.dat", data.getKey());
    final File output = new File(basedir, filename);

    try {
      final boolean success = output.mkdirs();
      if (!success) {
        throw new IllegalStateException(filename);
      }
    } catch (Exception e) {
      throw new IOException("cannot make directories", e);
    }

    final Path path = Files.write(output.toPath(), data.getValue(), StandardOpenOption.CREATE,
        StandardOpenOption.WRITE);

  }
}
