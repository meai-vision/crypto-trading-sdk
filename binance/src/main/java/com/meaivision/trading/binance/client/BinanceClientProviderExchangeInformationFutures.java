package com.meaivision.trading.binance.client;

import com.meaivision.trading.base.service.ClientProvider;
import java.util.Optional;

public class BinanceClientProviderExchangeInformationFutures
    implements ClientProvider<Optional<Object>, BinanceClientExchangeInformationFutures> {

  @Override
  public BinanceClientExchangeInformationFutures get(Optional<Object> properties) {
    return new BinanceClientExchangeInformationFutures();
  }
}
