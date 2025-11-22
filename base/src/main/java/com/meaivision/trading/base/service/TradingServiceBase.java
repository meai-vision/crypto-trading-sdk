package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.fundamental.OrderRequest;
import com.meaivision.trading.base.model.fundamental.OrderResponse;
import com.meaivision.trading.base.model.TradingClientSettings;
import java.util.List;

public interface TradingServiceBase<
    ORDER_REQUEST extends OrderRequest, ORDER_RESPONSE extends OrderResponse> {

  List<ORDER_RESPONSE> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings);

  ORDER_RESPONSE createOrder(ORDER_REQUEST orderRequest, TradingClientSettings settings);

  void closeOrder(Long orderId, String symbol, TradingClientSettings settings);

  void closeMultipleOrder(List<Long> orderIds, String symbol, TradingClientSettings settings);

  void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings);
}
