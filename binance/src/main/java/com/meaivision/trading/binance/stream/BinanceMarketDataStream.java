package com.meaivision.trading.binance.stream;

import com.binance.connector.client.WebSocketStreamClient;
import com.binance.connector.client.impl.WebSocketStreamClientImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.stream.BookTickerEvent;
import com.meaivision.trading.base.stream.MarketDataStream;
import com.meaivision.trading.base.stream.StreamException;
import com.meaivision.trading.base.stream.StreamSubscription;
import com.meaivision.trading.base.stream.TradeEvent;
import com.meaivision.trading.base.util.JsonUtils;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 * Binance Spot market-data WebSocket streams.
 *
 * <p>Implementation notes:
 *
 * <ul>
 *   <li>Uses {@link WebSocketStreamClientImpl} from {@code binance-connector-java}.
 *   <li>Binance symbol names on stream URIs must be <strong>lowercase</strong> (e.g.
 *       {@code btcusdt}). We lower-case for you.
 *   <li>Trade streams: prefer {@code aggTrade} (fewer, deduplicated messages, lower CPU). Use
 *       {@link #subscribeTrade} which routes to aggTrade; if you need raw per-trade detail call
 *       {@link #subscribeRawTrade}.
 *   <li>Parsing is intentionally minimal and direct — no DTO allocation per message beyond the
 *       {@link TradeEvent}/{@link BookTickerEvent} value object.
 * </ul>
 */
@Slf4j
public class BinanceMarketDataStream implements MarketDataStream {

  private final WebSocketStreamClient streamClient;

  public BinanceMarketDataStream() {
    this(new WebSocketStreamClientImpl());
  }

  public BinanceMarketDataStream(String wsBaseUrl) {
    this(new WebSocketStreamClientImpl(wsBaseUrl));
  }

  public BinanceMarketDataStream(WebSocketStreamClient streamClient) {
    this.streamClient = streamClient;
  }

  @Override
  public StreamSubscription subscribeTrade(String symbol, Consumer<TradeEvent> handler) {
    String lower = symbol.toLowerCase();
    int id =
        streamClient.aggTradeStream(
            lower,
            data -> {
              try {
                handler.accept(parseAggTrade(data));
              } catch (Throwable t) {
                log.error("aggTrade handler failed for {}", lower, t);
              }
            });
    log.info("Subscribed to aggTrade stream symbol={} id={}", lower, id);
    return new BinanceStreamSubscription(id, streamClient);
  }

  /** Raw per-trade subscription (one event per match). Higher message rate than aggTrade. */
  public StreamSubscription subscribeRawTrade(String symbol, Consumer<TradeEvent> handler) {
    String lower = symbol.toLowerCase();
    int id =
        streamClient.tradeStream(
            lower,
            data -> {
              try {
                handler.accept(parseRawTrade(data));
              } catch (Throwable t) {
                log.error("trade handler failed for {}", lower, t);
              }
            });
    log.info("Subscribed to trade stream symbol={} id={}", lower, id);
    return new BinanceStreamSubscription(id, streamClient);
  }

  @Override
  public StreamSubscription subscribeBookTicker(
      String symbol, Consumer<BookTickerEvent> handler) {
    String lower = symbol.toLowerCase();
    int id =
        streamClient.bookTicker(
            lower,
            data -> {
              try {
                handler.accept(parseBookTicker(data));
              } catch (Throwable t) {
                log.error("bookTicker handler failed for {}", lower, t);
              }
            });
    log.info("Subscribed to bookTicker stream symbol={} id={}", lower, id);
    return new BinanceStreamSubscription(id, streamClient);
  }

  @Override
  public void close() {
    streamClient.closeAllConnections();
  }

  private static TradeEvent parseAggTrade(String raw) {
    // {"e":"aggTrade","E":..,"s":"BTCUSDT","a":..,"p":"..","q":"..","f":..,"l":..,"T":..,"m":bool}
    JsonNode n = JsonUtils.convertToJsonTree(raw);
    return TradeEvent.builder()
        .eventTimeMs(n.path("E").asLong())
        .tradeTimeMs(n.path("T").asLong())
        .receivedNanos(System.nanoTime())
        .symbol(n.path("s").asText())
        .tradeId(n.path("a").asLong())
        .price(parseDouble(n.path("p").asText()))
        .quantity(parseDouble(n.path("q").asText()))
        .buyerIsMaker(n.path("m").asBoolean())
        .build();
  }

  private static TradeEvent parseRawTrade(String raw) {
    // {"e":"trade","E":..,"s":"..","t":..,"p":"..","q":"..","T":..,"m":bool}
    JsonNode n = JsonUtils.convertToJsonTree(raw);
    return TradeEvent.builder()
        .eventTimeMs(n.path("E").asLong())
        .tradeTimeMs(n.path("T").asLong())
        .receivedNanos(System.nanoTime())
        .symbol(n.path("s").asText())
        .tradeId(n.path("t").asLong())
        .price(parseDouble(n.path("p").asText()))
        .quantity(parseDouble(n.path("q").asText()))
        .buyerIsMaker(n.path("m").asBoolean())
        .build();
  }

  private static BookTickerEvent parseBookTicker(String raw) {
    // {"u":..,"s":"..","b":"..","B":"..","a":"..","A":".."}    (no event time on individual stream)
    JsonNode n = JsonUtils.convertToJsonTree(raw);
    return BookTickerEvent.builder()
        .eventTimeMs(System.currentTimeMillis())
        .receivedNanos(System.nanoTime())
        .symbol(n.path("s").asText())
        .bestBid(parseDouble(n.path("b").asText()))
        .bestBidQty(parseDouble(n.path("B").asText()))
        .bestAsk(parseDouble(n.path("a").asText()))
        .bestAskQty(parseDouble(n.path("A").asText()))
        .build();
  }

  private static double parseDouble(String s) {
    if (s == null || s.isEmpty()) {
      return Double.NaN;
    }
    try {
      return Double.parseDouble(s);
    } catch (NumberFormatException e) {
      throw new StreamException("Failed to parse double: " + s, e);
    }
  }
}
