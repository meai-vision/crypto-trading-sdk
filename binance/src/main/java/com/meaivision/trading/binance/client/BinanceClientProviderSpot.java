package com.meaivision.trading.binance.client;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;

public class BinanceClientProviderSpot
    implements ClientProvider<TradingClientSettings, SpotClient> {

  @Override
  public SpotClient get(TradingClientSettings properties) {
    return new SpotClientImpl(properties.getApiKey(), properties.getSecretKey());
  }
}
