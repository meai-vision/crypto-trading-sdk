package com.meaivision.trading.binance.stream;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.exceptions.BinanceClientException;
import com.binance.connector.client.exceptions.BinanceConnectorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.stream.StreamException;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.BinanceConstants;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages a Binance Spot listenKey lifecycle:
 *
 * <ul>
 *   <li>obtains a listenKey via REST {@code POST /api/v3/userDataStream},
 *   <li>schedules a keep-alive ping every 30 minutes (Binance closes after 60),
 *   <li>can be {@link #close() closed} cleanly — also issues {@code DELETE} to free the key.
 * </ul>
 */
@Slf4j
public class BinanceListenKeyManager implements AutoCloseable {

  private static final long KEEPALIVE_PERIOD_MIN = 30L;

  private final ClientProvider<TradingClientSettings, SpotClient> clientProvider;
  private final TradingClientSettings settings;
  private final ScheduledExecutorService scheduler;
  private final AtomicBoolean closed = new AtomicBoolean(false);

  private volatile String listenKey;
  private ScheduledFuture<?> keepAlive;

  public BinanceListenKeyManager(
      ClientProvider<TradingClientSettings, SpotClient> clientProvider,
      TradingClientSettings settings) {
    this(
        clientProvider,
        settings,
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r, "binance-listenkey-keepalive");
              t.setDaemon(true);
              return t;
            }));
  }

  public BinanceListenKeyManager(
      ClientProvider<TradingClientSettings, SpotClient> clientProvider,
      TradingClientSettings settings,
      ScheduledExecutorService scheduler) {
    this.clientProvider = clientProvider;
    this.settings = settings;
    this.scheduler = scheduler;
  }

  public synchronized String start() {
    if (closed.get()) {
      throw new IllegalStateException("listenKey manager is closed");
    }
    this.listenKey = createListenKey();
    log.info("Obtained Binance listenKey={}…", mask(listenKey));
    this.keepAlive =
        scheduler.scheduleAtFixedRate(
            this::extendListenKeySafe,
            KEEPALIVE_PERIOD_MIN,
            KEEPALIVE_PERIOD_MIN,
            TimeUnit.MINUTES);
    return listenKey;
  }

  public String currentKey() {
    String k = listenKey;
    if (k == null) {
      throw new IllegalStateException("listenKey not yet started");
    }
    return k;
  }

  private String createListenKey() {
    SpotClient client = clientProvider.get(settings);
    try {
      String resp = client.createUserData().createListenKey();
      JsonNode node = JsonUtils.convertToJsonTree(resp);
      String key = node.path("listenKey").asText(null);
      if (key == null || key.isEmpty()) {
        throw new StreamException("Binance did not return a listenKey: " + resp);
      }
      return key;
    } catch (BinanceClientException | BinanceConnectorException e) {
      throw new StreamException("Failed to create Binance listenKey", e);
    }
  }

  private void extendListenKeySafe() {
    try {
      extendListenKey();
    } catch (Throwable t) {
      log.error("listenKey keep-alive failed — will retry next tick", t);
    }
  }

  private void extendListenKey() {
    String k = listenKey;
    if (k == null) return;
    SpotClient client = clientProvider.get(settings);
    LinkedHashMap<String, Object> params = new LinkedHashMap<>();
    params.put(BinanceConstants.PARAM_LISTEN_KEY, k);
    try {
      client.createUserData().extendListenKey(params);
      log.debug("Extended Binance listenKey={}…", mask(k));
    } catch (BinanceClientException | BinanceConnectorException e) {
      throw new StreamException("Failed to extend Binance listenKey", e);
    }
  }

  @Override
  public synchronized void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    if (keepAlive != null) {
      keepAlive.cancel(false);
      keepAlive = null;
    }
    String k = listenKey;
    if (k != null) {
      try {
        SpotClient client = clientProvider.get(settings);
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put(BinanceConstants.PARAM_LISTEN_KEY, k);
        client.createUserData().closeListenKey(params);
        log.info("Closed Binance listenKey={}…", mask(k));
      } catch (Throwable t) {
        log.warn("Failed to close Binance listenKey cleanly", t);
      }
    }
    scheduler.shutdownNow();
  }

  private static String mask(String key) {
    if (key == null || key.length() < 6) return "***";
    return key.substring(0, 6);
  }
}
