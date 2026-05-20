package com.meaivision.trading.base.stream;

import java.util.function.Consumer;

/**
 * Real-time market-data WebSocket stream. Implementations MUST be thread-safe: subscribe / close
 * can be called from any thread, callbacks are invoked from an internal I/O thread.
 *
 * <p>Callbacks SHOULD be cheap (parse + enqueue) — long-running work must hop to a worker thread
 * to avoid backpressuring the WebSocket reader.
 */
public interface MarketDataStream extends AutoCloseable {

  /** Subscribe to per-trade (or aggregated-trade) updates for the given symbol. */
  StreamSubscription subscribeTrade(String symbol, Consumer<TradeEvent> handler);

  /** Subscribe to best-bid / best-ask snapshots for the given symbol. */
  StreamSubscription subscribeBookTicker(String symbol, Consumer<BookTickerEvent> handler);

  /** Close all open connections. Idempotent. */
  @Override
  void close();
}
