package org.github.jarrad.mempoolspace;

public interface MempoolSpaceClient {

  RecommendedFeesResponse getRecommendedFees();

  String broadcast(final byte[] tx);

  interface RecommendedFeesResponse {

    Integer getFastestFee();

    Integer getHalfHourFee();

     Integer getHourFee();

     Integer getMinimumFee();
  }

}
