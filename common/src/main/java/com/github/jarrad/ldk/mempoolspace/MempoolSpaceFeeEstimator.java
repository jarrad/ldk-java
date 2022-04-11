package com.github.jarrad.ldk.mempoolspace;

import static java.util.Objects.requireNonNull;

import com.github.jarrad.ldk.InMemoryCachingFeeEstimator;
import java.util.function.Function;
import org.github.jarrad.mempoolspace.MempoolSpaceClient;
import org.github.jarrad.mempoolspace.MempoolSpaceClient.RecommendedFeesResponse;
import org.ldk.enums.ConfirmationTarget;
import org.ldk.structs.FeeEstimator;
import org.ldk.structs.FeeEstimator.FeeEstimatorInterface;

/**
 * Use the <em>mempool.space</em> API to load fee estimations.
 */
public class MempoolSpaceFeeEstimator implements FeeEstimatorInterface {

  public static FeeEstimator of(final MempoolSpaceClient client) {
    final MempoolSpaceFeeEstimator mempoolSpaceFeeEstimator = new MempoolSpaceFeeEstimator(client);
    final InMemoryCachingFeeEstimator cachingFeeEstimator = new InMemoryCachingFeeEstimator(
        mempoolSpaceFeeEstimator);
    return FeeEstimator.new_impl(cachingFeeEstimator);
  }

  /**
   * The default operation to calculate the normal fee is to average the half hour and hour fee.
   */
  private static final Function<RecommendedFeesResponse, Integer> DEFAULT_NORMAL_OP = response ->
      (response.getHalfHourFee() + response.getHourFee()) / 2;

  private final MempoolSpaceClient mempoolSpaceClient;

  private final Function<RecommendedFeesResponse, Integer> normalFeeOp;

  public MempoolSpaceFeeEstimator(final MempoolSpaceClient mempoolSpaceClient) {
    this(mempoolSpaceClient, DEFAULT_NORMAL_OP);
  }

  public MempoolSpaceFeeEstimator(final MempoolSpaceClient mempoolSpaceClient,
      final Function<RecommendedFeesResponse, Integer> normalFeeOp) {
    this.mempoolSpaceClient = requireNonNull(mempoolSpaceClient);
    this.normalFeeOp = requireNonNull(normalFeeOp);
  }

  /**
   * Pull fees from the given <em>mempool.space</em> API and convert to sat per 1000 weight.
   */
  @Override
  public int get_est_sat_per_1000_weight(final ConfirmationTarget confirmationTarget) {
    final RecommendedFeesResponse response = mempoolSpaceClient.getRecommendedFees();
    switch (confirmationTarget) {
      case LDKConfirmationTarget_Background:
        return convert(response.getMinimumFee());
      case LDKConfirmationTarget_HighPriority:
        return convert(response.getFastestFee());
      default:
        // default to "normal"
        final int normalFee = normalFeeOp.apply(response);
        return convert(normalFee);
    }
  }

  /**
   * Convert the given sat/vbyte value into the preferred sat per 1000 weight unit ensuring a 253
   * minimum.
   */
  private static int convert(final int satsPerVbyte) {
    return Math.max(253, satsPerVbyte * 250);
  }

}
