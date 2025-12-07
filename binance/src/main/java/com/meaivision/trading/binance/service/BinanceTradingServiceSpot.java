package com.meaivision.trading.binance.service;

import com.binance.connector.client.SpotClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.SpotOrder;
import com.meaivision.trading.base.model.SpotOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceSpot;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.BinanceConstants;
import com.meaivision.trading.binance.exception.BinanceException;
import com.meaivision.trading.binance.model.BinanceSpotOrder;
import com.meaivision.trading.binance.model.mapper.BinanceSpotOrderMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceTradingServiceSpot implements TradingServiceSpot {

  private final BinanceSpotOrderMapper binanceSpotOrderMapper;
  private final ClientProvider<TradingClientSettings, SpotClient> clientProvider;

  public BinanceTradingServiceSpot(
      BinanceSpotOrderMapper binanceSpotOrderMapper,
      ClientProvider<TradingClientSettings, SpotClient> clientProvider) {
    this.binanceSpotOrderMapper = binanceSpotOrderMapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public List<SpotOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, symbol);
    SpotClient spotClient = clientProvider.get(settings);
    try {
      String spotOrdersBySymbol = spotClient.createTrade().myTrades(parameters);
      JsonNode spotOrdersNode = JsonUtils.convertToJsonTree(spotOrdersBySymbol);
      return StreamSupport.stream(spotOrdersNode.spliterator(), false)
          .map(this::mapToModel)
          .toList();
    } catch (BinanceClientException | BinanceConnectorException e) {
      throw new BinanceException("Can't query all spot orders for symbol " + symbol, e);
    }
  }

  @Override
  public SpotOrder createOrder(SpotOrderRequest request, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, request.getSymbol());
    parameters.put(BinanceConstants.PARAM_SIDE, request.getSide());
    parameters.put(BinanceConstants.PARAM_TYPE, request.getType());
    SpotClient spotClient = clientProvider.get(settings);
    try {
      String newOrder = spotClient.createTrade().newOrder(parameters);
      JsonNode newOrderNode = JsonUtils.convertToJsonTree(newOrder);
      return mapToModel(newOrderNode);
    } catch (BinanceClientException | BinanceConnectorException e) {
      throw new BinanceException("Can't create spot order " + request, e);
    }
  }

  @Override
  public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);
    parameters.put("orderId", orderId);
    SpotClient client = clientProvider.get(settings);
    try {
      String result = client.createTrade().cancelOrder(parameters);
      log.debug("Spot order closing result: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException(
          "Can't close spot order for order ID '%d' and symbol '%s'".formatted(orderId, symbol), e);
    }
  }

  @Override
  public void closeMultipleOrder(
      List<Long> orderIds, String symbol, TradingClientSettings settings) {
    String ids = JsonUtils.convertToJson(orderIds);
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);
    parameters.put("orderIdList", ids);
    SpotClient client = clientProvider.get(settings);
    try {
      String result = client.createTrade().cancelOrder(parameters);
      log.debug("Closing spor orders result: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException(
          "Can't close multiple spot orders by IDs: %s for symbol %s".formatted(ids, symbol), e);
    }
  }

  @Override
  public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);
    SpotClient client = clientProvider.get(settings);
    try {
      String result = client.createTrade().cancelOpenOrders(parameters);
      log.debug("Closing all spot orders for symbol result: {}", result);
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Can't close all open spot orders for symbol " + symbol, e);
    }
  }

  private SpotOrder mapToModel(JsonNode newOrderNode) {
    BinanceSpotOrder binanceSpotOrder =
        JsonUtils.convertToObject(newOrderNode, BinanceSpotOrder.class);
    return binanceSpotOrderMapper.toModel(binanceSpotOrder);
  }
}
