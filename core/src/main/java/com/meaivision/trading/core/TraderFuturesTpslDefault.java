package com.meaivision.trading.core;

import com.meaivision.trading.base.model.AccountInfo;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.FuturesTpslOrder;
import com.meaivision.trading.base.model.FuturesTpslOrderRequest;
import com.meaivision.trading.base.model.RiskManagementLimit;
import com.meaivision.trading.base.model.RiskValues;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.TradingContext;
import com.meaivision.trading.base.model.enums.MarketDirection;
import com.meaivision.trading.base.model.enums.OrderType;
import com.meaivision.trading.base.model.enums.SideType;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.RiskManagementCalculator;
import com.meaivision.trading.base.service.TradingServiceFuturesTpsl;
import com.meaivision.trading.base.trader.TraderFuturesTpsl;
import java.math.BigDecimal;

public class TraderFuturesTpslDefault implements TraderFuturesTpsl {

  private final TradingServiceFuturesTpsl futuresTpslTradingService;
  private final RiskManagementCalculator riskManagementCalculator;
  private final AccountService<AccountInfo> accountService;

  public TraderFuturesTpslDefault(
      TradingServiceFuturesTpsl futuresTpslTradingService,
      RiskManagementCalculator riskManagementCalculator,
      AccountService<AccountInfo> accountService) {
    this.futuresTpslTradingService = futuresTpslTradingService;
    this.riskManagementCalculator = riskManagementCalculator;
    this.accountService = accountService;
  }

  @Override
  public FuturesTpslOrder createOrder(
      TradingContext tradingContext, TradingClientSettings tradingClientSettings) {
    FuturesTpslOrderRequest orderRequest = formRequest(tradingContext, tradingClientSettings);
    return futuresTpslTradingService.createTpslOrder(orderRequest, tradingClientSettings);
  }

  @Override
  public void closeOrder(FuturesTpslOrder futuresTpslOrder, TradingClientSettings settings) {
    futuresTpslTradingService.closeTpslOrder(futuresTpslOrder, settings);
  }

  private FuturesTpslOrderRequest formRequest(
      TradingContext tradingContext, TradingClientSettings tradingClientSettings) {

    RiskManagementLimit riskValues =
        calculateRiskManagementValues(tradingContext, tradingClientSettings);

    MarketDirection marketDirection = tradingContext.getMarketDirection();
    String side =
        marketDirection.equals(MarketDirection.HIGH)
            ? SideType.BUY.getStringValue()
            : SideType.SELL.getStringValue();
    BigDecimal tpPrice =
        marketDirection.equals(MarketDirection.HIGH)
            ? riskValues.getHighestValue()
            : riskValues.getLowestValue();
    BigDecimal slPrice =
        marketDirection.equals(MarketDirection.HIGH)
            ? riskValues.getLowestValue()
            : riskValues.getHighestValue();

    String ticker = tradingContext.getTicker();

    FuturesOrderRequest order =
        FuturesOrderRequest.builder()
            .symbol(ticker)
            .side(side)
            .type(OrderType.MARKET.getStringValue())
            .quantity(riskValues.getOrderTotal())
            .build();

    String expandedSide =
        marketDirection.equals(MarketDirection.HIGH)
            ? SideType.SELL.getStringValue()
            : SideType.BUY.getStringValue();
    FuturesOrderRequest stop =
        FuturesOrderRequest.builder()
            .symbol(ticker)
            .side(expandedSide)
            .type(OrderType.STOP_MARKET.getStringValue())
            .quantity(riskValues.getOrderTotal())
            .stopPrice(slPrice)
            .build();

    FuturesOrderRequest profit =
        FuturesOrderRequest.builder()
            .symbol(ticker)
            .side(expandedSide)
            .type(OrderType.TAKE_PROFIT_MARKET.getStringValue())
            .quantity(riskValues.getOrderTotal())
            .stopPrice(tpPrice)
            .build();

    FuturesTpslOrderRequest futuresTpslOrderRequest = new FuturesTpslOrderRequest();
    futuresTpslOrderRequest.setMainOrder(order);
    futuresTpslOrderRequest.setStopLossOrder(stop);
    futuresTpslOrderRequest.setTakeProfitOrder(profit);
    futuresTpslOrderRequest.setTicker(ticker);

    return futuresTpslOrderRequest;
  }

  private RiskManagementLimit calculateRiskManagementValues(
      TradingContext tradingContext, TradingClientSettings settings) {
    AccountInfo accountInfo = accountService.getAccountInfo(settings);
    BigDecimal availableBalance = accountInfo.getAvailableBalance();
    BigDecimal price = tradingContext.getPrice();
    int quantityPrecision = tradingContext.getQuantityPrecision();
    String ticker = tradingContext.getTicker();
    RiskValues riskValues = tradingContext.getRiskValues();
    int leverage = riskValues.getLeverage();
    BigDecimal firstPriceChangeIndex = riskValues.getFirstPriceChangeIndex();
    BigDecimal secondPriceChangeIndex = riskValues.getSecondPriceChangeIndex();
    BigDecimal balanceIndexPerOrder = riskValues.getBalanceIndexPerOrder();
    return riskManagementCalculator.calculate(
        ticker,
        price,
        availableBalance,
        quantityPrecision,
        leverage,
        firstPriceChangeIndex,
        secondPriceChangeIndex,
        balanceIndexPerOrder);
  }
}
