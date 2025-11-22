package com.meaivision.trading.base.trader;

import com.meaivision.trading.base.model.FuturesTpslOrder;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.TradingContext;

public interface TraderFuturesTpsl {

  FuturesTpslOrder createOrder(TradingContext tradingContext, TradingClientSettings settings);

  void closeOrder(FuturesTpslOrder futuresTpslOrder, TradingClientSettings settings);
}
