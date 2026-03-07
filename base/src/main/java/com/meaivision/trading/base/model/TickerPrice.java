package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Data transfer object representing the price of a financial instrument at a specific moment.
 *
 * <p>This class is used to encapsulate market data retrieved from external providers within the
 * MEAI Vision trading ecosystem.
 */
@Data
public class TickerPrice {

  /** The unique identifier for the asset (e.g., "BTCUSDT"). */
  private String ticker;

  /**
   * The current market price of the asset.
   *
   * <p>Represented as a {@link BigDecimal} to ensure high precision for financial calculations and
   * to avoid floating-point rounding issues.
   */
  private BigDecimal price;

  private TickerPrice(String ticker, BigDecimal price) {
    this.ticker = ticker;
    this.price = price;
  }

  /**
   * Static factory method to create a new TickerPrice instance.
   *
   * <p>Provides a clean and readable way to instantiate price data during the ingestion process
   * from public APIs.
   *
   * @param ticker the asset identifier
   * @param price the current market price
   * @return a new {@link TickerPrice} instance
   */
  public static TickerPrice of(String ticker, BigDecimal price) {
    return new TickerPrice(ticker, price);
  }
}
