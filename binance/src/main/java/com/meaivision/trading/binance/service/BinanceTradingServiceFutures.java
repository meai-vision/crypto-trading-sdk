package com.meaivision.trading.binance.service;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceFutures;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.BinanceConstants;
import com.meaivision.trading.binance.exception.BinanceException;
import com.meaivision.trading.binance.model.BinanceFuturesOrder;
import com.meaivision.trading.binance.model.mapper.BinanceFuturesOrderMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceTradingServiceFutures implements TradingServiceFutures {

  private final BinanceFuturesOrderMapper binanceFuturesOrderMapper;
  private final ClientProvider<TradingClientSettings, FuturesClient> clientProvider;

  public BinanceTradingServiceFutures(
      BinanceFuturesOrderMapper binanceFuturesOrderMapper,
      ClientProvider<TradingClientSettings, FuturesClient> clientProvider) {
    this.binanceFuturesOrderMapper = binanceFuturesOrderMapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public List<FuturesOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, symbol);
    FuturesClient client = clientProvider.get(settings);
    try {
      String result = client.account().allOrders(parameters);
      log.debug("Orders for symbol result: {}", result);
      return getMultipleFuturesOrderResponse(result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Can't find all futures orders for symbol " + symbol, e);
    }
  }

  @Override
  public FuturesOrder createOrder(FuturesOrderRequest request, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, request.getSymbol());
    parameters.put(BinanceConstants.PARAM_SIDE, request.getSide());
    parameters.put(BinanceConstants.PARAM_TYPE, request.getType());
    if (request.getStopPrice() != null) {
      parameters.put(BinanceConstants.PARAM_STOP_PRICE, request.getStopPrice());
    }
    parameters.put(BinanceConstants.PARAM_QUANTITY, request.getQuantity());
    FuturesClient client = clientProvider.get(settings);
    try {
      String result = client.account().newOrder(parameters);
      log.debug("Order creation result: {}", result);
      return getFuturesOrderResponse(result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Can't create futures order " + request, e);
    }
  }

  @Override
  public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);
    parameters.put("orderId", orderId);
    FuturesClient client = clientProvider.get(settings);
    try {
      String result = client.account().cancelOrder(parameters);
      log.debug("Closing order result: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException(
          "Can't close futures order by ID '%d' for symbol '%s'".formatted(orderId, symbol), e);
    }
  }

  @Override
  public void closeMultipleOrder(
      List<Long> orderIds, String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    String orderIdList = JsonUtils.convertToJson(orderIds);
    parameters.put("symbol", symbol);
    parameters.put("orderIdList", orderIdList);
    FuturesClient client = clientProvider.get(settings);
    try {
      String result = client.account().cancelMultipleOrders(parameters);
      log.debug("Closing multiple orders result: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Can't close multiple futures orders by IDs: " + orderIdList, e);
    }
  }

  @Override
  public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);
    FuturesClient client = clientProvider.get(settings);
    try {
      String result = client.account().cancelAllOpenOrders(parameters);
      log.debug("Close all order for symbol results: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Can't close all open futures orders for symbol " + symbol, e);
    }
  }

  private List<FuturesOrder> getMultipleFuturesOrderResponse(String result) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
    return StreamSupport.stream(jsonNode.spliterator(), false)
        .map(this::getFuturesOrderResponseByNode)
        .toList();
  }

  private FuturesOrder getFuturesOrderResponse(String result) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
    return getFuturesOrderResponseByNode(jsonNode);
  }

  private FuturesOrder getFuturesOrderResponseByNode(JsonNode jsonNode) {
    BinanceFuturesOrder binanceFuturesOrder =
        JsonUtils.convertToObject(jsonNode, BinanceFuturesOrder.class);
    return binanceFuturesOrderMapper.toModel(binanceFuturesOrder);
  }
}
