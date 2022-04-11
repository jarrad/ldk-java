package org.github.jarrad.mempoolspace.feign;

import com.google.common.io.BaseEncoding;
import feign.Feign;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.github.jarrad.mempoolspace.MempoolSpaceClient;

public class FeignMempoolSpaceClient implements MempoolSpaceClient {

  private static AtomicReference<FMempoolSpaceClient> _INSTANCE = new AtomicReference<>();

  private static String DEFAULT_API_URL = "https://mempool.space/api/";

  private static String API_URL;

  static {
    API_URL = Optional.ofNullable(System.getenv("MEMPOOL_API_URL")).orElse(DEFAULT_API_URL);
  }

  private static FMempoolSpaceClient build() {
    return Feign.builder()
        .decoder(new JacksonDecoder())
        .encoder(new JacksonEncoder())
        .target(FMempoolSpaceClient.class, API_URL);
  }

  /**
   * We only need to build the feign client one time and then reuse it.
   */
  private static FMempoolSpaceClient getInstance() {
    _INSTANCE.compareAndSet(null, build());
    return _INSTANCE.get();
  }

  public FeignMempoolSpaceClient() {
  }

  @Override
  public FeignRecommendedFeesResponse getRecommendedFees() {
    return getInstance().getRecommendedFees();
  }

  @Override
  public String broadcast(byte[] tx) {
    final String hex = BaseEncoding.base16().lowerCase().encode(tx);
    return getInstance().broadcast(hex);
  }

  /**
   * Internal interface used to ensure the deserialization of the response.
   */
  interface FMempoolSpaceClient {

    @RequestLine("GET /v1/fees/recommended")
    FeignRecommendedFeesResponse getRecommendedFees();

    @RequestLine("POST /tx")
    String broadcast(final String tx);
  }

  /**
   * Internal object used by feign/jackson to deserialize the response from <em>mempool.space</em>.
   */
  private static class FeignRecommendedFeesResponse implements RecommendedFeesResponse {

    private Integer fastestFee;

    private Integer halfHourFee;

    private Integer hourFee;

    private Integer minimumFee;

    private FeignRecommendedFeesResponse() {
      // deserialization only
    }

    @Override
    public Integer getFastestFee() {
      return fastestFee;
    }

    @Override
    public Integer getHalfHourFee() {
      return halfHourFee;
    }

    @Override
    public Integer getHourFee() {
      return hourFee;
    }

    @Override
    public Integer getMinimumFee() {
      return minimumFee;
    }

    @Override
    public String toString() {
      return "FeignRecommendedFeesResponse{" +
          "fastestFee=" + fastestFee +
          ", halfHourFee=" + halfHourFee +
          ", hourFee=" + hourFee +
          ", minimumFee=" + minimumFee +
          '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FeignRecommendedFeesResponse that = (FeignRecommendedFeesResponse) o;
      return Objects.equals(fastestFee, that.fastestFee) && Objects
          .equals(halfHourFee, that.halfHourFee) && Objects.equals(hourFee, that.hourFee)
          && Objects.equals(minimumFee, that.minimumFee);
    }

    @Override
    public int hashCode() {
      return Objects.hash(fastestFee, halfHourFee, hourFee, minimumFee);
    }
  }

}
