package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.FuturesTpslOrder;
import com.meaivision.trading.base.model.FuturesTpslOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;

public interface TradingServiceFuturesTpsl {
  FuturesTpslOrder createTpslOrder(FuturesTpslOrderRequest request, TradingClientSettings settings);

  void closeTpslOrder(FuturesTpslOrder futuresTpslOrder, TradingClientSettings settings);
}
