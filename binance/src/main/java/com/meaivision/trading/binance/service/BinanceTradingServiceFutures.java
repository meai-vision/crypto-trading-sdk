package com.meaivision.trading.binance.service;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meaivision.trading.binance.BinanceConstants;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceFutures;
import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceTradingServiceFutures implements TradingServiceFutures {

  private final ObjectMapper objectMapper;
  private final ClientProvider<TradingClientSettings, FuturesClient> clientProvider;

  public BinanceTradingServiceFutures(
      ObjectMapper objectMapper,
      ClientProvider<TradingClientSettings, FuturesClient> clientProvider) {
    this.objectMapper = objectMapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public List<FuturesOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {
    FuturesClient client = clientProvider.get(settings);

    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put(BinanceConstants.PARAM_SYMBOL, symbol);

    String result = null;
    try {
      result = client.account().allOrders(parameters);
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

    return getMultipleFuturesOrderResponse(result);
  }

  @Override
  public FuturesOrder createOrder(FuturesOrderRequest request, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    FuturesClient client = clientProvider.get(settings);

    parameters.put(BinanceConstants.PARAM_SYMBOL, request.getSymbol());
    parameters.put(BinanceConstants.PARAM_SIDE, request.getSide());
    parameters.put(BinanceConstants.PARAM_TYPE, request.getType());

    if (request.getStopPrice() != null) {
      parameters.put(BinanceConstants.PARAM_STOP_PRICE, request.getStopPrice());
    }
    parameters.put(BinanceConstants.PARAM_QUANTITY, request.getQuantity());

    String result = null;
    try {
      result = client.account().newOrder(parameters);
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

    return getFuturesOrderResponse(result);
  }

  @Override
  public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    FuturesClient client = clientProvider.get(settings);

    parameters.put("symbol", symbol);
    parameters.put("orderId", orderId);

    String result = null;
    try {
      result = client.account().cancelOrder(parameters);
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
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    FuturesClient client = clientProvider.get(settings);

    String value = null;
    try {
      value = objectMapper.writeValueAsString(orderIds);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    parameters.put("symbol", symbol);
    parameters.put("orderIdList", value);

    String result = null;
    try {
      result = client.account().cancelMultipleOrders(parameters);
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
  public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {

    FuturesClient client = clientProvider.get(settings);

    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    parameters.put("symbol", symbol);

    String result = null;
    try {
      result = client.account().cancelAllOpenOrders(parameters);
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

    // todo: add database deleting
  }

  private List<FuturesOrder> getMultipleFuturesOrderResponse(String result) {
    JsonNode jsonNode = null;
    try {
      jsonNode = objectMapper.readTree(result);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't process Binance response!", e);
    }

    List<FuturesOrder> responses = new ArrayList<>();
    for (JsonNode node : jsonNode) {
      FuturesOrder futuresOrderByNode = getFuturesOrderResponseByNode(node);
      responses.add(futuresOrderByNode);
    }
    return responses;
  }

  private FuturesOrder getFuturesOrderResponse(String result) {
    JsonNode jsonNode = null;
    try {
      jsonNode = objectMapper.readTree(result);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Can't process Binance response!", e);
    }
    return getFuturesOrderResponseByNode(jsonNode);
  }

  private FuturesOrder getFuturesOrderResponseByNode(JsonNode jsonNode) {
    FuturesOrder futuresOrder = objectMapper.convertValue(jsonNode, FuturesOrder.class);
    return futuresOrder;
  }
}
