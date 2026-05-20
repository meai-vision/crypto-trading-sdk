package com.meaivision.trading.base.stream;

import lombok.Builder;
import lombok.Value;

/**
 * Single trade tick from a market trade stream (aggTrade or per-trade).
 *
 * <p>Numeric fields are exposed as primitive {@code double} / {@code long} on purpose: this DTO is
 * on the HFT hot path and consumers should avoid {@link java.math.BigDecimal} until they need to
 * persist or expose the value over an API.
 */
@Value
@Builder
public class TradeEvent {
  /** Exchange-side event timestamp in milliseconds since epoch. */
  long eventTimeMs;

  /** Exchange-side trade timestamp in milliseconds since epoch. */
  long tradeTimeMs;

  /** Local receipt timestamp in nanoseconds ({@code System.nanoTime()}). */
  long receivedNanos;

  String symbol;
  long tradeId;
  double price;
  double quantity;

  /** {@code true} if the buyer was the market-maker (i.e. seller was aggressor). */
  boolean buyerIsMaker;
}
