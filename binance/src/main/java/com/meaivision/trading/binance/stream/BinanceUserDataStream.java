package com.meaivision.trading.binance.stream;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.stream.OrderExecutionEvent;
import com.meaivision.trading.base.stream.StreamSubscription;
import com.meaivision.trading.base.stream.UserDataEvent;
import com.meaivision.trading.base.stream.UserDataStream;
import com.meaivision.trading.base.util.JsonUtils;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * Binance Spot User Data Stream.
 *
 * <p>Lifecycle: {@link #subscribe(TradingClientSettings, Consumer)} creates a listen-key, opens the
 * WS, and routes parsed {@link UserDataEvent}s to the handler. {@link #close()} tears everything
 * down.
 *
 * <p>Parses {@code executionReport} events only (sufficient for the HFT scalper strategy).
 * Non-executionReport messages are logged at DEBUG and dropped.
 */
@Slf4j
public class BinanceUserDataStream implements UserDataStream {

  private final ClientProvider<TradingClientSettings, SpotClient> clientProvider;
  private final WebSocketStreamClient streamClient;
  private BinanceListenKeyManager listenKeyManager;
  private BinanceStreamSubscription wsSub;

  public BinanceUserDataStream(ClientProvider<TradingClientSettings, SpotClient> clientProvider) {
    this(clientProvider, new WebSocketStreamClientImpl());
  }

  public BinanceUserDataStream(
      ClientProvider<TradingClientSettings, SpotClient> clientProvider, String wsBaseUrl) {
    this(clientProvider, new WebSocketStreamClientImpl(wsBaseUrl));
  }

  public BinanceUserDataStream(
      ClientProvider<TradingClientSettings, SpotClient> clientProvider,
      WebSocketStreamClient streamClient) {
    this.clientProvider = clientProvider;
    this.streamClient = streamClient;
  }

  @Override
  public synchronized StreamSubscription subscribe(
      TradingClientSettings settings, Consumer<UserDataEvent> handler) {
    if (wsSub != null) {
      throw new IllegalStateException("User Data Stream is already subscribed");
    }
    this.listenKeyManager = new BinanceListenKeyManager(clientProvider, settings);
    String key = listenKeyManager.start();
    int id =
        streamClient.listenUserStream(
            key,
            data -> {
              try {
                UserDataEvent event = parse(data);
                if (event != null) {
                  handler.accept(event);
                }
              } catch (Throwable t) {
                log.error("user-data handler failed: {}", data, t);
              }
            });
    log.info("Subscribed to Binance User Data Stream id={}", id);
    this.wsSub = new BinanceStreamSubscription(id, streamClient);
    return wsSub;
  }

  private static UserDataEvent parse(String raw) {
    JsonNode n = JsonUtils.convertToJsonTree(raw);
    String eventType = n.path("e").asText();
    if ("executionReport".equals(eventType)) {
      return OrderExecutionEvent.builder()
          .eventTimeMs(n.path("E").asLong())
          .transactionTimeMs(n.path("T").asLong())
          .receivedNanos(System.nanoTime())
          .symbol(n.path("s").asText())
          .orderId(n.path("i").asLong())
          .clientOrderId(n.path("c").asText())
          .originalClientOrderId(n.path("C").asText())
          .side(n.path("S").asText())
          .orderType(n.path("o").asText())
          .timeInForce(n.path("f").asText())
          .status(n.path("X").asText())
          .executionType(n.path("x").asText())
          .orderPrice(parseDouble(n.path("p").asText()))
          .orderQty(parseDouble(n.path("q").asText()))
          .lastFilledQty(parseDouble(n.path("l").asText()))
          .cumulativeFilledQty(parseDouble(n.path("z").asText()))
          .lastFilledPrice(parseDouble(n.path("L").asText()))
          .commission(parseDouble(n.path("n").asText()))
          .commissionAsset(n.path("N").asText())
          .build();
    }
    if (log.isDebugEnabled()) {
      log.debug("Ignoring user-data event type={}", eventType);
    }
    return null;
  }

  private static double parseDouble(String s) {
    if (s == null || s.isEmpty()) {
      return 0.0;
    }
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException ignored) {
      return 0.0;
    }
  }

  @Override
  public synchronized void close() {
    if (wsSub != null) {
      wsSub.close();
      wsSub = null;
    }
    if (listenKeyManager != null) {
      listenKeyManager.close();
      listenKeyManager = null;
    }
  }
}
