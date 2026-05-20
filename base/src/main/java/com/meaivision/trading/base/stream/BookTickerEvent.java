package com.meaivision.trading.base.stream;

import lombok.Builder;
import lombok.Value;

/** Best bid/ask snapshot from a {@code bookTicker} WebSocket stream. */
@Value
@Builder
public class BookTickerEvent {
  long eventTimeMs;
  long receivedNanos;
  String symbol;
  double bestBid;
  double bestBidQty;
  double bestAsk;
  double bestAskQty;
}
