package com.meaivision.trading.core;

import com.meaivision.trading.base.service.RiskManagementCalculator;
import com.meaivision.trading.base.model.RiskManagementLimit;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class RiskManagementCalculatorDefault implements RiskManagementCalculator {

  @Override
  public RiskManagementLimit calculate(
      String ticker,
      BigDecimal tickerPrice,
      BigDecimal availableBalance,
      int maxQuantityPrecision,
      int leverage,
      BigDecimal firstPriceChangeIndex,
      BigDecimal secondPriceChangeIndex,
      BigDecimal balanceIndexPerOrder) {

    BigDecimal leverageMultiplicand = BigDecimal.valueOf(leverage);

    BigDecimal maxRealOrderAmount = availableBalance.multiply(balanceIndexPerOrder);
    BigDecimal leveragedOrderAmount = maxRealOrderAmount.multiply(leverageMultiplicand);
    BigDecimal quantity =
        leveragedOrderAmount
            .divide(tickerPrice, MathContext.DECIMAL32)
            .setScale(maxQuantityPrecision, RoundingMode.HALF_UP);

    BigDecimal lowPriceFraction = maxRealOrderAmount.multiply(firstPriceChangeIndex);
    BigDecimal lowPriceAmount =
        maxRealOrderAmount.subtract(lowPriceFraction).multiply(leverageMultiplicand);
    BigDecimal lowPrice = lowPriceAmount.divide(quantity, MathContext.DECIMAL32);

    BigDecimal highPriceFraction = maxRealOrderAmount.multiply(secondPriceChangeIndex);
    BigDecimal highPriceAmount =
        maxRealOrderAmount.add(highPriceFraction).multiply(leverageMultiplicand);
    BigDecimal highPrice = highPriceAmount.divide(quantity, MathContext.DECIMAL32);

    return RiskManagementLimit.builder()
        .tickerName(ticker)
        .highestValue(scalePrice(tickerPrice, highPrice))
        .lowestValue(scalePrice(tickerPrice, lowPrice))
        .orderTotal(quantity)
        .build();
  }

  private BigDecimal scalePrice(BigDecimal priceExample, BigDecimal priceToScale) {
    int priceScale = priceExample.scale();
    return priceToScale.setScale(priceScale, RoundingMode.HALF_UP);
  }
}
