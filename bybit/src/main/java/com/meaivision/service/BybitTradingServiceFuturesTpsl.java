package com.meaivision.service;

import com.meaivision.service.BybitTradingServiceFutures;
import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.FuturesTpslOrder;
import com.meaivision.trading.base.model.FuturesTpslOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.enums.OrderType;
import com.meaivision.trading.base.model.enums.SideType;
import com.meaivision.trading.base.service.TradingServiceFuturesTpsl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
public class BybitTradingServiceFuturesTpsl implements TradingServiceFuturesTpsl {

    private final BybitTradingServiceFutures tradingServiceFutures;

    public BybitTradingServiceFuturesTpsl(BybitTradingServiceFutures tradingServiceFutures) {
        this.tradingServiceFutures = tradingServiceFutures;
    }

    @Override
    public FuturesTpslOrder createTpslOrder(FuturesTpslOrderRequest request, TradingClientSettings settings) {

        FuturesOrder main = tradingServiceFutures.createOrder(request.getMainOrder(), settings);
        FuturesOrder sl = tradingServiceFutures.createOrder(request.getStopLossOrder(), settings);
        FuturesOrder tp = tradingServiceFutures.createOrder(request.getTakeProfitOrder(), settings);

        FuturesTpslOrder futuresTpslOrder = new FuturesTpslOrder();
        futuresTpslOrder.setTicker(request.getTicker());
        futuresTpslOrder.setMainOrder(main);
        futuresTpslOrder.setStopLossOrder(sl);
        futuresTpslOrder.setTakeProfitOrder(tp);

        return futuresTpslOrder;
    }

    @Override
    public void closeTpslOrder(FuturesTpslOrder futuresTpslOrder, TradingClientSettings settings) {

        FuturesOrder mainOrder = futuresTpslOrder.getMainOrder();
        FuturesOrder stopLossOrder = futuresTpslOrder.getStopLossOrder();
        FuturesOrder takeProfitOrder = futuresTpslOrder.getTakeProfitOrder();

        SideType side = SideType.valueOf(mainOrder.getSide());
        SideType invertedSide = side.equals(SideType.BUY) ? SideType.SELL : SideType.BUY;

        BigDecimal quantity = new BigDecimal(mainOrder.getQuantity());

        FuturesOrderRequest closeMainOrderRequest = FuturesOrderRequest.builder()
                .symbol(mainOrder.getSymbol())
                .type(OrderType.MARKET.getStringValue())
                .side(invertedSide.getStringValue())
                .quantity(quantity)
                .build();

        tradingServiceFutures.createOrder(closeMainOrderRequest, settings);

        List<Long> ids = List.of(
                Long.valueOf(stopLossOrder.getId()),
                Long.valueOf(takeProfitOrder.getId())
        );

        tradingServiceFutures.closeMultipleOrder(ids, mainOrder.getSymbol(), settings);
    }
}