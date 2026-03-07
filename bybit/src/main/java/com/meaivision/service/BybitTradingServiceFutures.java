package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.model.BybitFuturesOrder;
import com.meaivision.model.mapper.BybitFuturesOrderMapper;
import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.base.model.FuturesOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceFutures;
import com.meaivision.trading.base.util.JsonUtils;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BybitTradingServiceFutures implements TradingServiceFutures {

  private final BybitFuturesOrderMapper mapper;
  private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

  public BybitTradingServiceFutures(
      BybitFuturesOrderMapper mapper,
      ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
    this.mapper = mapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public List<FuturesOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {

    BybitRestClient client = clientProvider.get(settings);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, symbol);
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

    String response;

    try {
      response = client.sendGet("/v5/order/realtime", params);
      log.info("Bybit query orders raw response: {}", response);
    } catch (IOException e) {
      throw new BybitException("Can't query futures orders for symbol " + symbol, e);
    }

    return parseOrdersList(response);
  }

  @Override
  public FuturesOrder createOrder(FuturesOrderRequest request, TradingClientSettings settings) {

    BybitRestClient client = clientProvider.get(settings);

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, request.getSymbol());
    params.put(BybitConstants.PARAM_SIDE, request.getSide());
    params.put(BybitConstants.PARAM_TYPE, request.getType());
    params.put(BybitConstants.PARAM_QUANTITY, request.getQuantity().toString());
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

    if (request.getPrice() != null) {
      params.put("price", request.getPrice().toString());
    }

    if (request.getStopPrice() != null) {
      params.put(BybitConstants.PARAM_STOP_PRICE, request.getStopPrice().toString());
    }

    String response;

    try {
      response = client.sendPost("/v5/order/create", params);
      log.info("Bybit create order response: {}", response);
    } catch (IOException e) {
      throw new BybitException("Can't create futures order " + request, e);
    }

    String orderId = extractOrderId(response);

    log.info("Created Bybit orderId: {}", orderId);

    return fetchCreatedOrder(orderId, request.getSymbol(), settings);
  }

  @Override
  public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {

    closeOrder(orderId.toString(), symbol, settings);
  }

  public void closeOrder(String orderId, String symbol, TradingClientSettings settings) {
    BybitRestClient client = clientProvider.get(settings);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, symbol);
    params.put("orderId", orderId);
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
    try {
      String response = client.sendPost("/v5/order/cancel", params);
      log.info("Bybit cancel order response: {}", response);
    } catch (IOException e) {
      throw new BybitException(
          "Can't close futures order '" + orderId + "' for symbol '" + symbol + "'", e);
    }
  }

  @Override
  public void closeMultipleOrder(
      List<Long> orderIds, String symbol, TradingClientSettings settings) {
    BybitRestClient client = clientProvider.get(settings);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, symbol);
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
    params.put("orderIdList", JsonUtils.convertToJson(orderIds));
    try {
      String response = client.sendPost("/v5/order/cancel-batch", params);
      log.info("Bybit cancel batch response: {}", response);
    } catch (IOException e) {
      throw new BybitException("Can't close multiple futures orders " + orderIds, e);
    }
  }

  @Override
  public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {
    BybitRestClient client = clientProvider.get(settings);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, symbol);
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
    try {
      String response = client.sendPost("/v5/order/cancel-all", params);
      log.info("Bybit cancel all response: {}", response);
    } catch (IOException e) {
      throw new BybitException("Can't close all orders for " + symbol, e);
    }
  }

  private List<FuturesOrder> parseOrdersList(String response) {
    JsonNode root = JsonUtils.convertToJsonTree(response);
    JsonNode listNode = root.path("result").path("list");
    if (listNode.isMissingNode()) {
      log.warn("Bybit order list empty: {}", response);
      return List.of();
    }
    return StreamSupport.stream(listNode.spliterator(), false)
        .map(this::mapOrderNode)
        .filter(Objects::nonNull)
        .toList();
  }

  private FuturesOrder mapOrderNode(JsonNode node) {
    log.info("Bybit order node: {}", node);
    BybitFuturesOrder order = JsonUtils.convertToObject(node, BybitFuturesOrder.class);
    log.info("Mapped BybitFuturesOrder: {}", order);
    return mapper.toModel(order);
  }

  private String extractOrderId(String response) {
    JsonNode root = JsonUtils.convertToJsonTree(response);
    JsonNode orderIdNode = root.path("result").path("orderId");
    if (orderIdNode.isMissingNode()) {
      throw new BybitException("Bybit response missing orderId: " + response);
    }
    return orderIdNode.asText();
  }

  private FuturesOrder fetchCreatedOrder(
      String orderId, String symbol, TradingClientSettings settings) {
    BybitRestClient client = clientProvider.get(settings);
    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_SYMBOL, symbol);
    params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
    try {
      String response = client.sendGet("/v5/order/realtime", params);
      log.info("Bybit fetch orders response: {}", response);
      JsonNode listNode = JsonUtils.convertToJsonTree(response).path("result").path("list");
      for (JsonNode node : listNode) {
        if (orderId.equals(node.path("orderId").asText())) {
          BybitFuturesOrder order = JsonUtils.convertToObject(node, BybitFuturesOrder.class);
          log.info("Fetched created order from realtime: {}", order);
          return mapper.toModel(order);
        }
      }
      log.warn("Order not found in realtime orders list, fallback to history: {}", orderId);
      params.put("orderId", orderId);
      response = client.sendGet("/v5/order/history", params);
      log.info("Bybit fetch order history response: {}", response);
      listNode = JsonUtils.convertToJsonTree(response).path("result").path("list");
      if (!listNode.isEmpty()) {
        JsonNode node = listNode.get(0);
        BybitFuturesOrder order = JsonUtils.convertToObject(node, BybitFuturesOrder.class);
        log.info("Fetched order from history: {}", order);
        return mapper.toModel(order);
      }
      BybitFuturesOrder fallbackOrder = new BybitFuturesOrder();
      fallbackOrder.setOrderId(orderId);
      fallbackOrder.setSymbol(symbol);
      return mapper.toModel(fallbackOrder);

    } catch (IOException e) {
      throw new BybitException("Failed to fetch created order " + orderId, e);
    }
  }
}
