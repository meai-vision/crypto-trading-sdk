package com.meaivision.service;

import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.model.BybitWalletInfo;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;
import java.io.IOException;

public class BybitWalletService {

    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitWalletService(ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    public BybitWalletInfo getWalletInfo(TradingClientSettings settings) {
        BybitRestClient client = clientProvider.get(settings);
        try {
            String response = client.sendGet("/v5/account/wallet-balance", null);
            com.fasterxml.jackson.databind.JsonNode jsonNode =
                    JsonUtils.convertToJsonTree(response);
            return JsonUtils.convertToObject(jsonNode, BybitWalletInfo.class);
        } catch (IOException e) {
            throw new BybitException("Can't get wallet information for client", e);
        }
    }
}