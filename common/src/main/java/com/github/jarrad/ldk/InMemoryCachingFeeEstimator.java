package com.github.jarrad.ldk;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.FeeEstimator.FeeEstimatorInterface;

/**
 * Use an in-memory cache to hold fees for a specified duration.
 */
public class InMemoryCachingFeeEstimator implements FeeEstimatorInterface {

  private static final Duration DEFAULT_EXPIRATION_DURATION = Duration.ofMillis(10);

  private final FeeEstimatorInterface delegate;

  private final LoadingCache<ConfirmationTarget, Integer> cache;

  private final Duration feeExpirationDuration;

  public InMemoryCachingFeeEstimator(final FeeEstimatorInterface delegate) {
    this(delegate, DEFAULT_EXPIRATION_DURATION);
  }

  public InMemoryCachingFeeEstimator(final FeeEstimatorInterface delegate,
      final Duration feeExpirationDuration) {
    this.delegate = requireNonNull(delegate);
    this.feeExpirationDuration = requireNonNull(feeExpirationDuration);
    cache = CacheBuilder.newBuilder()
        .expireAfterWrite(feeExpirationDuration)
        .build(cacheLoader());
  }

  private CacheLoader<ConfirmationTarget, Integer> cacheLoader() {
    return new CacheLoader<>() {
      @Override
      public Integer load(final ConfirmationTarget confirmationTarget) throws Exception {
        return delegate.get_est_sat_per_1000_weight(confirmationTarget);
      }
    };
  }

  @Override
  public int get_est_sat_per_1000_weight(final ConfirmationTarget confirmationTarget) {
    try {
      return cache.get(confirmationTarget);
    } catch (ExecutionException e) {
      final Throwable root = Throwables.getRootCause(e);
      if (root instanceof IOException) {
        // failed to load fees
        throw new RuntimeException("failed to load fees", root);
      }
      throw new RuntimeException("Unknown error", root);
    }
  }
}
