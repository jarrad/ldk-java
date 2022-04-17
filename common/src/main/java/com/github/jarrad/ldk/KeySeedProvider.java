package com.github.jarrad.ldk;

import java.util.function.Supplier;

/**
 * A provider of the key seed.
 */
public interface KeySeedProvider extends Supplier<byte[]> {

}
