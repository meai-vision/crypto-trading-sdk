package com.meaivision.trading.binance.client;

import com.meaivision.trading.base.service.ClientProvider;

import java.util.Optional;

public class ClientProviderExchangeInformationFutures
    implements ClientProvider<Optional<Object>, ClientProviderExchangeInformationFutures> {

  @Override
  public ClientProviderExchangeInformationFutures get(Optional<Object> properties) {
    return new ClientProviderExchangeInformationFutures();
  }
}
