package com.meaivision.trading.binance.stream;

import com.binance.connector.client.WebSocketStreamClient;
import com.meaivision.trading.base.stream.StreamSubscription;
import java.util.concurrent.atomic.AtomicBoolean;

class BinanceStreamSubscription implements StreamSubscription {

  private final int connectionId;
  private final WebSocketStreamClient streamClient;
  private final AtomicBoolean closed = new AtomicBoolean(false);

  BinanceStreamSubscription(int connectionId, WebSocketStreamClient streamClient) {
    this.connectionId = connectionId;
    this.streamClient = streamClient;
  }

  @Override
  public int id() {
    return connectionId;
  }

  @Override
  public void close() {
    if (closed.compareAndSet(false, true)) {
      streamClient.closeConnection(connectionId);
    }
  }
}
