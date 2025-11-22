package com.meaivision.trading.base;

import com.meaivision.trading.base.model.RiskManagementLimit;
import com.meaivision.trading.base.service.RiskManagementCalculator;
import com.meaivision.trading.core.RiskManagementCalculatorDefault;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RiskManagementCalculatorDefaultTest {

  private static final String TICKER_NAME = "ASSETQUOTE";
  private static final BigDecimal TICKER_PRICE = BigDecimal.valueOf(210.67);

  private final RiskManagementCalculator riskCalculator;

  {
    riskCalculator = new RiskManagementCalculatorDefault();
  }

  @Test
  public void calculate_validData_ok() {

    BigDecimal expectedLowestPrice = BigDecimal.valueOf(208.42);
    BigDecimal expectedHighestPrice = BigDecimal.valueOf(212.63);
    BigDecimal expectedQuantity = BigDecimal.valueOf(5.7);

    RiskManagementLimit expected =
        RiskManagementLimit.builder()
            .tickerName(TICKER_NAME)
            .lowestValue(expectedLowestPrice)
            .highestValue(expectedHighestPrice)
            .orderTotal(expectedQuantity)
            .build();

    BigDecimal balance = BigDecimal.valueOf(1000);
    RiskManagementLimit actual =
        riskCalculator.calculate(
            TICKER_NAME,
            TICKER_PRICE,
            balance,
            1,
            12,
            BigDecimal.valueOf(0.01),
            BigDecimal.valueOf(0.01),
            BigDecimal.valueOf(0.1));

    Assertions.assertNotNull(actual);
    Assertions.assertEquals(expected, actual, "Actual risk values calculated incorrectly!");
  }
}
