package com.meaivision.trading.binance.service;

import com.binance.connector.client.wallet.rest.api.CapitalApi;
import com.binance.connector.client.wallet.rest.model.AllCoinsInformationResponse;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;

public class BinanceWalletService {

  private final ClientProvider<TradingClientSettings, CapitalApi> clientProvider;

  public BinanceWalletService(ClientProvider<TradingClientSettings, CapitalApi> clientProvider) {
    this.clientProvider = clientProvider;
  }

  public AllCoinsInformationResponse get(TradingClientSettings tradingClientSettings) {
    CapitalApi capitalApi = clientProvider.get(tradingClientSettings);
    return capitalApi.allCoinsInformation(null).getData();
  }
}
