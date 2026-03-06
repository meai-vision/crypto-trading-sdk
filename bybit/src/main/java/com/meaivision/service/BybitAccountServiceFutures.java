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
import java.math.BigDecimal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BybitAccountServiceFutures implements AccountService<BybitAccountInfoFutures> {

    private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

    public BybitAccountServiceFutures(ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public BybitAccountInfoFutures getAccountInfo(TradingClientSettings settings) {
        BybitRestClient client = clientProvider.get(settings);
        String accountType = resolveAccountType(client);
        String walletResponse =
                sendGetRequest(client, "/v5/account/wallet-balance", Map.of(BybitConstants.PARAM_ACCOUNT_TYPE, accountType));
        String positionResponse =
                sendGetRequest(
                        client,
                        "/v5/position/list",
                        Map.of("category", "linear", "settleCoin", "USDT", BybitConstants.PARAM_ACCOUNT_TYPE, accountType));

        return calculateAccountInfo(walletResponse, positionResponse);
    }

    private String resolveAccountType(BybitRestClient client) {
        try {
            String response = client.sendGet("/v5/account/info", null);
            JsonNode statusNode = JsonUtils.convertToJsonTree(response).path("result").path("unifiedMarginStatus");
            return statusNode.asInt() == 0
                    ? BybitConstants.ACCOUNT_TYPE_CONTRACT
                    : BybitConstants.ACCOUNT_TYPE_UNIFIED;
        } catch (IOException e) {
            throw new BybitException("Failed to resolve Bybit account type", e);
        }
    }

    private String sendGetRequest(BybitRestClient client, String path, Map<String, String> params) {
        try {
            String result = client.sendGet(path, params);
            log.debug("{} response: {}", path, result);
            return result;
        } catch (IOException e) {
            throw new BybitException("Error during request to " + path, e);
        }
    }

    private BybitAccountInfoFutures calculateAccountInfo(String walletResponse, String positionResponse) {
        BigDecimal walletUsdt = extractWalletBalance(walletResponse);
        BigDecimal usedMargin = extractPositionSum(positionResponse, "positionMargin");
        BigDecimal unrealizedPnL = extractPositionSum(positionResponse, "unrealisedPnl");
        BigDecimal availableBalance = walletUsdt.subtract(usedMargin).add(unrealizedPnL).max(BigDecimal.ZERO);
        BybitAccountInfoFutures info = new BybitAccountInfoFutures();
        info.setTotalWalletBalance(walletUsdt);
        info.setTotalCrossWalletBalance(walletUsdt);
        info.setTotalMarginBalance(walletUsdt);
        info.setAvailableBalance(availableBalance);
        return info;
    }

    private BigDecimal extractWalletBalance(String walletResponse) {
        JsonNode coins = JsonUtils.convertToJsonTree(walletResponse)
                .path("result")
                .path("list")
                .get(0)
                .path("coin");

        if (!coins.isArray()) {
            return BigDecimal.ZERO;
        }

        for (JsonNode coin : coins) {
            if ("USDT".equalsIgnoreCase(coin.path("coin").asText())) {
                return toBigDecimal(coin.path("walletBalance"));
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal extractPositionSum(String positionResponse, String field) {
        JsonNode positions = JsonUtils.convertToJsonTree(positionResponse).path("result").path("list");
        if (!positions.isArray()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (JsonNode pos : positions) {
            sum = sum.add(toBigDecimal(pos.path(field)));
        }
        return sum;
    }

    private BigDecimal toBigDecimal(JsonNode node) {
        if (node == null || node.isMissingNode() || node.asText().isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(node.asText());
    }
}