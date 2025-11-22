package com.meaivision.trading.binance.service;

import com.binance.connector.client.SpotClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meaivision.trading.binance.BinanceConstants;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceSpot;
import com.meaivision.trading.base.model.SpotOrder;
import com.meaivision.trading.base.model.SpotOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.util.JsonUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceTradingServiceSpot implements TradingServiceSpot {

  private final ObjectMapper objectMapper;
  private final ClientProvider<TradingClientSettings, SpotClient> clientProvider;

  public BinanceTradingServiceSpot(
      ObjectMapper objectMapper, ClientProvider<TradingClientSettings, SpotClient> clientProvider) {
    this.objectMapper = objectMapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public List<SpotOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {
    SpotClient spotClient = clientProvider.get(settings);

    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, symbol);

    String spotOrdersBySymbol = spotClient.createTrade().myTrades(parameters);
    JsonNode spotOrdersNode = JsonUtils.convertToJsonTree(spotOrdersBySymbol);

    List<SpotOrder> spotOrders = new ArrayList<>();
    spotOrdersNode.forEach(
        orderNode -> {
          SpotOrder spotOrder = JsonUtils.convertToObject(orderNode, SpotOrder.class);
          spotOrders.add(spotOrder);
        });

    return spotOrders;
  }

  @Override
  public SpotOrder createOrder(SpotOrderRequest request, TradingClientSettings settings) {
    SpotClient spotClient = clientProvider.get(settings);

    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, request.getSymbol());
    parameters.put(BinanceConstants.PARAM_SIDE, request.getSide());
    parameters.put(BinanceConstants.PARAM_TYPE, request.getType());

    String newOrder = spotClient.createTrade().newOrder(parameters);
    JsonNode newOrderNode = JsonUtils.convertToJsonTree(newOrder);
    SpotOrder spotOrder = JsonUtils.convertToObject(newOrderNode, SpotOrder.class);
    return spotOrder;
  }

  @Override
  public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    SpotClient client = clientProvider.get(settings);

    parameters.put("symbol", symbol);
    parameters.put("orderId", orderId);

    String result = null;
    try {
      result = client.createTrade().cancelOrder(parameters);
      log.debug(result);
    } catch (BinanceConnectorException e) {
      log.error("fullErrMessage: {}", e.getMessage(), e);
    } catch (BinanceClientException e) {
      log.error(
          "fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
          e.getMessage(),
          e.getErrMsg(),
          e.getErrorCode(),
          e.getHttpStatusCode(),
          e);
    }
  }

  @Override
  public void closeMultipleOrder(
      List<Long> orderIds, String symbol, TradingClientSettings settings) {

    SpotClient client = clientProvider.get(settings);

    orderIds.forEach(
        orderId -> {
          String value = null;
          try {
            value = objectMapper.writeValueAsString(orderIds);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }

          LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
          parameters.put("symbol", symbol);
          parameters.put("orderIdList", value);

          String result = null;
          try {
            result = client.createTrade().cancelOrder(parameters);
            log.debug(result);
          } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
          } catch (BinanceClientException e) {
            log.error(
                "fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                e.getMessage(),
                e.getErrMsg(),
                e.getErrorCode(),
                e.getHttpStatusCode(),
                e);
          }
        });
  }

  @Override
  public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {
    SpotClient client = clientProvider.get(settings);

    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);

    String result = null;
    try {
      result = client.createTrade().cancelOpenOrders(parameters);
      log.debug(result);
    } catch (BinanceConnectorException e) {
      log.error("fullErrMessage: {}", e.getMessage(), e);
    } catch (BinanceClientException e) {
      log.error(
          "fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
          e.getMessage(),
          e.getErrMsg(),
          e.getErrorCode(),
          e.getHttpStatusCode(),
          e);
    }
  }
}
