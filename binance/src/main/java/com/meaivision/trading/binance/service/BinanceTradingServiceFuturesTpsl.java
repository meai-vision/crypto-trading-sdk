package com.meaivision.trading.binance.service;

import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.FuturesTpslOrder;
import com.meaivision.trading.base.model.FuturesTpslOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.enums.OrderType;
import com.meaivision.trading.base.model.enums.SideType;
import com.meaivision.trading.base.service.TradingServiceFutures;
import com.meaivision.trading.base.service.TradingServiceFuturesTpsl;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinanceTradingServiceFuturesTpsl implements TradingServiceFuturesTpsl {

  private final TradingServiceFutures tradingServiceFutures;

  @Override
  public FuturesTpslOrder createTpslOrder(
      FuturesTpslOrderRequest request, TradingClientSettings settings) {
    FuturesOrderRequest mainOrder = request.getMainOrder();
    FuturesOrder main = tradingServiceFutures.createOrder(mainOrder, settings);

    FuturesOrderRequest stopLossOrder = request.getStopLossOrder();
    FuturesOrder sl = tradingServiceFutures.createOrder(stopLossOrder, settings);

    FuturesOrderRequest takeProfitOrder = request.getTakeProfitOrder();
    FuturesOrder tp = tradingServiceFutures.createOrder(takeProfitOrder, settings);

    FuturesTpslOrder futuresTpslOrder = new FuturesTpslOrder();
    futuresTpslOrder.setTicker(request.getTicker());
    futuresTpslOrder.setMainOrder(main);
    futuresTpslOrder.setTakeProfitOrder(tp);
    futuresTpslOrder.setStopLossOrder(sl);

    return futuresTpslOrder;
  }

  @Override
  public void closeTpslOrder(FuturesTpslOrder futuresTpslOrder, TradingClientSettings settings) {
    FuturesOrder mainOrder = futuresTpslOrder.getMainOrder();
    FuturesOrder stopLossOrder = futuresTpslOrder.getStopLossOrder();
    FuturesOrder takeProfitOrder = futuresTpslOrder.getTakeProfitOrder();

    String side = mainOrder.getSide();
    SideType invertedSideType = SideType.valueOf(side);
    if (invertedSideType.equals(SideType.BUY)) {
      invertedSideType = SideType.SELL;
    } else {
      invertedSideType = SideType.BUY;
    }

    BigDecimal quantity = BigDecimal.valueOf(Double.parseDouble(mainOrder.getOrigQty()));

    FuturesOrderRequest orderRequest =
        FuturesOrderRequest.builder()
            .symbol(mainOrder.getSymbol())
            .type(OrderType.MARKET.getStringValue())
            .side(invertedSideType.getStringValue())
            .quantity(quantity)
            .build();

    tradingServiceFutures.createOrder(orderRequest, settings);
    List<Long> ids = List.of(stopLossOrder.getOrderId(), takeProfitOrder.getOrderId());
    tradingServiceFutures.closeMultipleOrder(ids, mainOrder.getSymbol(), settings);
  }
}
