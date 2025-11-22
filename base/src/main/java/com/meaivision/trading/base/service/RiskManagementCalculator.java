package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.RiskManagementLimit;
import java.math.BigDecimal;

public interface RiskManagementCalculator {

  /**
   * Order total is a value that represents a quantity of ticker asset. With an initial amount:
   *
   * <p>TICK = 2 USDT <br>
   * Balance = 100 USDT <br>
   * Risk percentage = 0.1 We can calculate:
   *
   * <p>Maximum amount for fundamental (Max): 100 * 0.1 = 10 USDT <br>
   * To calculate sum for fundamental: Max / TICK. 10 / 2 = 5 TICK <br>
   * As a result, fundamental total (quantity) is 5.
   *
   * @param ticker an uppercase full name of ticker. Example: BTCUSDT.
   * @param tickerPrice a current price of ticker.
   * @param availableBalance total balance that is available for trading.
   * @param maxQuantityPrecision is a max number of symbols after comma for ticker.
   * @return an object with values representing quantity of asset, take profit and stop loss prices.
   */
  RiskManagementLimit calculate(
      String ticker,
      BigDecimal tickerPrice,
      BigDecimal availableBalance,
      int maxQuantityPrecision,
      int leverage,
      BigDecimal firstPriceChangeIndex,
      BigDecimal secondPriceChangeIndex,
      BigDecimal balanceIndexPerOrder);
}
