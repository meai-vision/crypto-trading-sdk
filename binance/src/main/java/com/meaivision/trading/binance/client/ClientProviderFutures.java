package com.meaivision.trading.binance.client;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.model.TradingClientSettings;

public class ClientProviderFutures implements ClientProvider<TradingClientSettings, FuturesClient> {

  @Override
  public FuturesClient get(TradingClientSettings properties) {
    return new UMFuturesClientImpl(properties.getApiKey(), properties.getSecretKey());
  }
}
