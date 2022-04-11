package org.github.jarrad.mempoolspace.feign;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.github.jarrad.mempoolspace.MempoolSpaceClient.RecommendedFeesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeignMempoolSpaceClientTest {

  private FeignMempoolSpaceClient subject;

  @BeforeEach
  void setUp() {
    subject = new FeignMempoolSpaceClient();
  }

  @Test
  public void itShouldGetRecommendedFees() {
    final RecommendedFeesResponse response = subject.getRecommendedFees();

    assertNotNull(response);
    assertTrue(response.getFastestFee() > 0);
    assertTrue(response.getHalfHourFee() > 0);
    assertTrue(response.getHourFee() > 0);
    assertTrue(response.getMinimumFee() > 0);
  }

}
