package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.TickerPrice;

/**
 * Interface for retrieving real-time market data within the MEAI Vision ecosystem.
 *
 * <p>This provider interacts with public APIs to fetch the most recent asset valuations. It serves
 * as a core component for the trading intelligence layer.
 *
 * @see TickerPrice
 */
public interface MarketDataProvider {

  /**
   * Retrieves the current market price for a specified ticker.
   *
   * <p>This method performs a request to an external liquidity provider or exchange API to obtain
   * the latest "Last Price" for the given asset identifier.
   *
   * @param ticker the unique identifier of the asset (e.g., "BTCUSDT")
   * @return a {@link TickerPrice} object containing the current price and associated metadata
   */
  TickerPrice getCurrentPrice(String ticker);
}
