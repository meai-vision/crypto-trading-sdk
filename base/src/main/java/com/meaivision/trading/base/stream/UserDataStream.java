package com.meaivision.trading.base.stream;

import com.meaivision.trading.base.model.TradingClientSettings;
import java.util.function.Consumer;

/**
 * Authenticated User Data Stream. Implementations:
 *
 * <ol>
 *   <li>obtain a listenKey via REST (using {@link TradingClientSettings}),
 *   <li>open the WS stream against that key,
 *   <li>schedule a 30-minute keep-alive ping,
 *   <li>auto-renew on disconnect.
 * </ol>
 *
 * Callbacks are invoked from an internal I/O thread — same hot-path discipline as {@link
 * MarketDataStream}.
 */
public interface UserDataStream extends AutoCloseable {

  /** Start the stream and route every parsed event to {@code handler}. */
  StreamSubscription subscribe(TradingClientSettings settings, Consumer<UserDataEvent> handler);

  /** Close all open connections. Idempotent. */
  @Override
  void close();
}
