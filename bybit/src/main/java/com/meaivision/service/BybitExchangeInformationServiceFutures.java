package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.trading.base.model.ExchangeInfo;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.ExchangeInformationServiceFutures;
import com.meaivision.trading.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
public class BybitExchangeInformationServiceFutures
        implements ExchangeInformationServiceFutures {

    private final ClientProvider<Optional<Object>, BybitRestClient> clientProvider;

    public BybitExchangeInformationServiceFutures(
            ClientProvider<Optional<Object>, BybitRestClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public List<ExchangeInfo> getSymbolsInformation() {
        BybitRestClient client = clientProvider.get(Optional.empty());
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_CATEGORY, BybitConstants.CATEGORY_LINEAR);
        String response = sendRequest(client, params);
        JsonNode symbolsNode = getSymbols(response);
        if (symbolsNode == null || symbolsNode.isMissingNode()) {
            throw new BybitException("Can't find exchange information for symbols!");
        }
        return StreamSupport.stream(symbolsNode.spliterator(), true)
                .map(node -> JsonUtils.convertToObject(node, ExchangeInfo.class))
                .filter(Objects::nonNull)
                .toList();
    }

    private String sendRequest(BybitRestClient client, LinkedHashMap<String, String> params) {

        try {
            String result = client.sendGet("/v5/market/instruments-info", params);
            log.debug("Bybit exchange information response: {}", result);
            return result;
        } catch (IOException e) {
            throw new BybitException(
                    "Error occurred during getting Bybit exchange information",
                    e
            );
        }
    }

    private JsonNode getSymbols(String exchangeInfoJson) {
        JsonNode jsonNode = JsonUtils.convertToJsonTree(exchangeInfoJson);
        return jsonNode.path(BybitConstants.FIELD_RESULT).path(BybitConstants.FIELD_LIST);
    }
}