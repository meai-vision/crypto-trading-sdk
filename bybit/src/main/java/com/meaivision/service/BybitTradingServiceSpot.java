package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.model.BybitSpotOrder;
import com.meaivision.model.mapper.BybitSpotOrderMapper;
import com.meaivision.trading.base.model.SpotOrder;
import com.meaivision.trading.base.model.SpotOrderRequest;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.TradingServiceSpot;
import com.meaivision.trading.base.util.JsonUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BybitTradingServiceSpot implements TradingServiceSpot {

    private final BybitSpotOrderMapper bybitSpotOrderMapper;
    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitTradingServiceSpot(
            BybitSpotOrderMapper bybitSpotOrderMapper,
            ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.bybitSpotOrderMapper = bybitSpotOrderMapper;
        this.clientProvider = clientProvider;
    }

    @Override
    public List<SpotOrder> queryAllOrdersForSymbol(String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);

        String response;
        try {
            response = client.sendGet("/v5/spot/order-history", params);
        } catch (IOException e) {
            throw new BybitException("Can't query all spot orders for symbol " + symbol, e);
        }

        JsonNode ordersNode = JsonUtils.convertToJsonTree(response);
        return StreamSupport.stream(ordersNode.spliterator(), false)
                .map(this::mapToModel)
                .toList();
    }

    @Override
    public SpotOrder createOrder(SpotOrderRequest request, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, request.getSymbol());
        params.put(BybitConstants.PARAM_SIDE, request.getSide());
        params.put(BybitConstants.PARAM_TYPE, request.getType());
        //todo fix this: params.put(BybitConstants.PARAM_QUANTITY, request.getQuantity().toString());

        String response;
        try {
            response = client.sendPost("/v5/spot/order", params);
        } catch (IOException e) {
            throw new BybitException("Can't create spot order " + request, e);
        }

        JsonNode orderNode = JsonUtils.convertToJsonTree(response);
        return mapToModel(orderNode);
    }

    @Override
    public void closeOrder(Long orderId, String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);
        params.put("orderId", String.valueOf(orderId));

        try {
            String result = client.sendPost("/v5/spot/cancel-order", params);
            log.debug("Spot order closing result: {}", result);
        } catch (IOException e) {
            throw new BybitException("Can't close spot order for order ID " + orderId + " and symbol " + symbol, e);
        }
    }

    @Override
    public void closeMultipleOrder(List<Long> orderIds, String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);
        params.put("orderIdList", JsonUtils.convertToJson(orderIds));

        try {
            String result = client.sendPost("/v5/spot/cancel-batch", params);
            log.debug("Closing multiple spot orders result: {}", result);
        } catch (IOException e) {
            throw new BybitException("Can't close multiple spot orders by IDs for symbol " + symbol, e);
        }
    }

    @Override
    public void closeAllOpenOrdersForSymbol(String symbol, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, symbol);

        try {
            String result = client.sendPost("/v5/spot/cancel-all", params);
            log.debug("Closing all spot orders for symbol result: {}", result);
        } catch (IOException e) {
            throw new BybitException("Can't close all open spot orders for symbol " + symbol, e);
        }
    }

    private SpotOrder mapToModel(JsonNode orderNode) {
        BybitSpotOrder bybitSpotOrder = JsonUtils.convertToObject(orderNode, BybitSpotOrder.class);
        return bybitSpotOrderMapper.toModel(bybitSpotOrder);
    }
}