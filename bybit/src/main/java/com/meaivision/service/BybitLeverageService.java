package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.trading.base.model.LeverageRequest;
import com.meaivision.trading.base.model.LeverageResponse;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.LeverageService;
import com.meaivision.trading.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class BybitLeverageService implements LeverageService<LeverageRequest, LeverageResponse> {
    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitLeverageService(ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public LeverageResponse changeInitialLeverage(LeverageRequest request, TradingClientSettings settings) {

        BybitRestClient client = clientProvider.get(settings);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_SYMBOL, request.getSymbol());
        params.put("leverage", String.valueOf(request.getLeverage()));
        params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);

        String response = sendRequest(client, params, request.getSymbol());

        return parseResponse(response);
    }

    private String sendRequest(BybitRestClient client, LinkedHashMap<String, String> params, String symbol) {
        try {
            String result = client.sendPost("/v5/position/set-leverage", params);
            log.debug("Bybit set leverage response for {}: {}", symbol, result);
            return result;
        } catch (IOException e) {
            throw new BybitException(
                    "Error occurred during changing initial leverage for " + symbol,
                    e
            );
        }
    }

    private LeverageResponse parseResponse(String response) {
        JsonNode jsonNode = JsonUtils.convertToJsonTree(response);
        LeverageResponse leverageResponse = new LeverageResponse();

        JsonNode resultNode = jsonNode.path(BybitConstants.FIELD_RESULT);
        if (!resultNode.isMissingNode()) {
            //toDO
        }

        return leverageResponse;
    }
}