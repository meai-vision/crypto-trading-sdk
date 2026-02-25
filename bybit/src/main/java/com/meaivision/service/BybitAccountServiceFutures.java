package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.model.BybitAccountInfoFutures;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BybitAccountServiceFutures
        implements AccountService<BybitAccountInfoFutures> {

    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitAccountServiceFutures(
            ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public BybitAccountInfoFutures getAccountInfo(TradingClientSettings settings) {
        BybitRestClient client = clientProvider.get(settings);
        String accountType = resolveAccountType(client);
        Map<String, String> params = new LinkedHashMap<>();
        params.put(BybitConstants.PARAM_ACCOUNT_TYPE, accountType);
        String response = sendWalletBalanceRequest(client, params);
        return toAccountInfo(response);
    }

    private String resolveAccountType(BybitRestClient client) {

        try {
            String response = client.sendGet("/v5/account/info", Map.of());
            JsonNode jsonNode = JsonUtils.convertToJsonTree(response);
            int status = jsonNode
                    .path(BybitConstants.FIELD_RESULT)
                    .path(BybitConstants.FIELD_UNIFIED_MARGIN_STATUS)
                    .asInt();
            if (status == 1) {
                return BybitConstants.ACCOUNT_TYPE_CONTRACT;
            }
            if (status == 2 || status == 3) {
                return BybitConstants.ACCOUNT_TYPE_UNIFIED;
            }
            throw new IllegalStateException(
                    "Unknown unifiedMarginStatus: " + status
            );
        } catch (IOException e) {
            throw new BybitException(
                    "Failed to resolve Bybit account type",
                    e
            );
        }
    }

    private String sendWalletBalanceRequest(
            BybitRestClient client,
            Map<String, String> params) {
        try {
            String result = client.sendGet(
                    "/v5/account/wallet-balance",
                    params
            );
            log.debug("Bybit wallet balance response: {}", result);
            return result;
        } catch (IOException e) {
            throw new BybitException(
                    "Error occurred during getting Bybit wallet balance",
                    e
            );
        }
    }

    private BybitAccountInfoFutures toAccountInfo(String response) {
        JsonNode jsonNode = JsonUtils.convertToJsonTree(response);
        return JsonUtils.convertToObject(
                jsonNode,
                BybitAccountInfoFutures.class
        );
    }
}