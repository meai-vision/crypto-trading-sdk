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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
public class BybitTradingServiceFutures implements TradingServiceFutures {

    private final BybitFuturesOrderMapper bybitFuturesOrderMapper;
    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitTradingServiceFutures(
            BybitFuturesOrderMapper bybitFuturesOrderMapper,
            ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.bybitFuturesOrderMapper = bybitFuturesOrderMapper;
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
            response = client.sendGet("/v5/order/realtime", params); // Bybit V5 query orders
            log.debug("Orders for symbol {} response: {}", symbol, response);
        } catch (IOException e) {
            throw new BybitException("Can't find all futures orders for symbol " + symbol, e);
        }

        return getMultipleFuturesOrderResponse(response);
    }

    @Override
    public FuturesOrder createOrder(FuturesOrderRequest request, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, request.getSymbol());
        params.put(BybitConstants.PARAM_SIDE, request.getSide());
        params.put(BybitConstants.PARAM_TYPE, request.getType());
        if (request.getStopPrice() != null) {
            params.put(BybitConstants.PARAM_STOP_PRICE, request.getStopPrice().toString());
        }
        params.put(BybitConstants.PARAM_QUANTITY, request.getQuantity().toString());
        params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

        String response;
        try {
            response = client.sendPost("/v5/order/create", params);
            log.debug("Order creation result: {}", response);
        } catch (IOException e) {
            throw new BybitException("Can't create futures order " + request, e);
        }

        return getFuturesOrderResponse(response);
    }

    @Override
    public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);
        params.put("orderId", String.valueOf(orderId));
        params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

        try {
            String response = client.sendPost("/v5/order/cancel", params);
            log.debug("Closing order result: {}", response);
        } catch (IOException e) {
            throw new BybitException(
                    "Can't close futures order by ID '" + orderId + "' for symbol '" + symbol + "'",
                    e
            );
        }
    }

    @Override
    public void closeMultipleOrder(List<Long> orderIds, String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);
        params.put("orderIdList", JsonUtils.convertToJson(orderIds));
        params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

        try {
            String response = client.sendPost("/v5/order/cancel-batch", params);
            log.debug("Closing multiple orders result: {}", response);
        } catch (IOException e) {
            throw new BybitException(
                    "Can't close multiple futures orders by IDs: " + orderIds, e
            );
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
            log.debug("Close all orders for symbol {} result: {}", symbol, response);
        } catch (IOException e) {
            throw new BybitException(
                    "Can't close all open futures orders for symbol " + symbol, e
            );
        }
    }

    private List<FuturesOrder> getMultipleFuturesOrderResponse(String result) {
        JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
        return StreamSupport.stream(jsonNode.spliterator(), false)
                .map(this::getFuturesOrderResponseByNode)
                .filter(Objects::nonNull)
                .toList();
    }

    private FuturesOrder getFuturesOrderResponse(String result) {
        JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
        return getFuturesOrderResponseByNode(jsonNode);
    }

    private FuturesOrder getFuturesOrderResponseByNode(JsonNode jsonNode) {
        BybitFuturesOrder bybitFuturesOrder =
                JsonUtils.convertToObject(jsonNode, BybitFuturesOrder.class);
        return bybitFuturesOrderMapper.toModel(bybitFuturesOrder);
    }
}