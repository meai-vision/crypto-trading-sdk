package com.meaivision.trading.base.stream;

/**
 * Handle returned when subscribing to a WebSocket stream. Cancel via {@link #close()} to stop
 * receiving callbacks and release the underlying connection slot.
 */
public interface StreamSubscription extends AutoCloseable {
  /** Implementation-defined identifier (e.g. okhttp connection id). */
  int id();

  /** Idempotent. Safe to call from any thread. */
  @Override
  void close();
}
