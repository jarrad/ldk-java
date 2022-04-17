package org.github.jarrad.mempoolspace.feign;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSource;
import com.google.common.primitives.Ints;
import feign.Feign;
import feign.FeignException;
import feign.RequestLine;
import feign.Response;
import feign.Response.Body;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.github.jarrad.mempoolspace.MempoolSpaceClient;

public class FeignMempoolSpaceClient implements MempoolSpaceClient {

  private static AtomicReference<FMempoolSpaceClient> _INSTANCE = new AtomicReference<>();

  private static AtomicReference<MempoolSpaceBlockClient> _BLOCK_INSTANCE = new AtomicReference<>();

  private static String DEFAULT_API_URL = "https://mempool.space/api/";

  private static String API_URL;

  static {
    API_URL = Optional.ofNullable(System.getenv("MEMPOOL_API_URL")).orElse(DEFAULT_API_URL);
  }

  private static FMempoolSpaceClient build() {
    final ObjectMapper mapper =
        new ObjectMapper()
            .findAndRegisterModules()
            .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
    return Feign.builder()
        .decoder(new JacksonDecoder(mapper))
        .encoder(new JacksonEncoder())
        .target(FMempoolSpaceClient.class, API_URL);
  }

  private static MempoolSpaceBlockClient buildBlock() {
    final ObjectMapper mapper =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .enable(JsonReadFeature.ALLOW_LEADING_ZEROS_FOR_NUMBERS.mappedFeature());
    return Feign.builder()
            .decoder(new BlockDecoder())
            .encoder(new JacksonEncoder())
            .target(MempoolSpaceBlockClient.class, API_URL);
  }

  /** We only need to build the feign client one time and then reuse it. */
  private static FMempoolSpaceClient getInstance() {
    _INSTANCE.compareAndSet(null, build());
    return _INSTANCE.get();
  }

  private static MempoolSpaceBlockClient getBlockClient() {
    _BLOCK_INSTANCE.compareAndSet(null, buildBlock());
    return _BLOCK_INSTANCE.get();
  }

  public FeignMempoolSpaceClient() {}

  @Override
  public FeignRecommendedFeesResponse getRecommendedFees() {
    return getInstance().getRecommendedFees();
  }

  @Override
  public String broadcast(byte[] tx) {
    final String hex = BaseEncoding.base16().lowerCase().encode(tx);
    return getInstance().broadcast(hex);
  }

  @Override
  public String getBlockTipHash() {
    return getBlockClient().getBlockTipHash();
  }

  @Override
  public int getBlockTipHeight() {
    return getBlockClient().getBlockTipHeight();
  }

  /** Internal interface used to ensure the deserialization of the response. */
  interface FMempoolSpaceClient {

    @RequestLine("GET /v1/fees/recommended")
    FeignRecommendedFeesResponse getRecommendedFees();

    @RequestLine("POST /tx")
    String broadcast(final String tx);

  }

  interface MempoolSpaceBlockClient {
    @RequestLine("GET /blocks/tip/height")
    int getBlockTipHeight();

    @RequestLine("GET /blocks/tip/hash")
    String getBlockTipHash();
  }

  static class BlockDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
      final Body body = response.body();
      final InputStream inputStream = body.asInputStream();
      final ByteSource source = new ByteSource() {
        @Override
        public InputStream openStream() throws IOException {
          return inputStream;
        }
      };

      final String text = source.asCharSource(StandardCharsets.UTF_8).read();
      if (type.getTypeName().equalsIgnoreCase("int")) {
        return Ints.tryParse(text);
      }
      return text;
    }
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
      return "FeignRecommendedFeesResponse{"
          + "fastestFee="
          + fastestFee
          + ", halfHourFee="
          + halfHourFee
          + ", hourFee="
          + hourFee
          + ", minimumFee="
          + minimumFee
          + '}';
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
      return Objects.equals(fastestFee, that.fastestFee)
          && Objects.equals(halfHourFee, that.halfHourFee)
          && Objects.equals(hourFee, that.hourFee)
          && Objects.equals(minimumFee, that.minimumFee);
    }

    @Override
    public int hashCode() {
      return Objects.hash(fastestFee, halfHourFee, hourFee, minimumFee);
    }
  }
}
