package com.meaivision.trading.bybit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.AccountInfoGeneral;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.bybit.exception.BybitException;
import com.meaivision.trading.bybit.model.BybitAccountInfoGeneral;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class BybitAccountServiceGeneral implements AccountService<BybitAccountInfoGeneral> {

  private static final String BASE_URL = "https://api.bybit.com";
  private static final String WALLET_BALANCE_URL = "/v5/account/wallet-balance";
  private static final String ALL_COINS_BALANCE_URL =
      "/v5/asset/transfer/query-account-coins-balance";

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Override
  public BybitAccountInfoGeneral getAccountInfo(TradingClientSettings settings) {
    try {
      String unifiedResponse = sendGetRequest(settings, WALLET_BALANCE_URL, "accountType=UNIFIED");
      List<AccountInfoGeneral> unifiedBalances = parseWallets(unifiedResponse);

      String fundResponse = sendGetRequest(settings, ALL_COINS_BALANCE_URL, "accountType=FUND");
      List<AccountInfoGeneral> fundBalances = parseAllCoinsWallets(fundResponse);

      Map<String, BigDecimal> mergedBalances = new HashMap<>();

      unifiedBalances.forEach(
          b -> mergedBalances.merge(b.getWallet(), b.getTotal(), BigDecimal::add));
      fundBalances.forEach(b -> mergedBalances.merge(b.getWallet(), b.getTotal(), BigDecimal::add));

      List<AccountInfoGeneral> finalWallets =
          mergedBalances.entrySet().stream()
              .map(
                  entry -> {
                    AccountInfoGeneral info = new AccountInfoGeneral();
                    info.setWallet(entry.getKey());
                    info.setTotal(entry.getValue());
                    return info;
                  })
              .collect(Collectors.toList());

      BybitAccountInfoGeneral result = new BybitAccountInfoGeneral();
      result.setWallets(finalWallets);
      return result;

    } catch (Exception e) {
      throw new BybitException("Exception occurred during getting unified Bybit account info", e);
    }
  }

  private String sendGetRequest(TradingClientSettings settings, String endpoint, String queryString)
      throws Exception {
    String apiKey = settings.getApiKey();
    String apiSecret = settings.getSecretKey();
    String timestamp = String.valueOf(Instant.now().toEpochMilli());
    String recvWindow = "5000";

    String payload = timestamp + apiKey + recvWindow + queryString;
    String signature = generateSignature(apiSecret, payload);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint + "?" + queryString))
            .GET()
            .header("X-BAPI-API-KEY", apiKey)
            .header("X-BAPI-SIGN", signature)
            .header("X-BAPI-TIMESTAMP", timestamp)
            .header("X-BAPI-RECV-WINDOW", recvWindow)
            .header("Content-Type", "application/json")
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  private String generateSignature(String secret, String payload) throws Exception {
    Mac hmacSha256 = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec =
        new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    hmacSha256.init(secretKeySpec);
    byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
    return HexFormat.of().formatHex(hash);
  }

  private List<AccountInfoGeneral> parseWallets(String jsonBody) {
    List<AccountInfoGeneral> balances = new ArrayList<>();
    JsonNode rootNode = JsonUtils.convertToJsonTree(jsonBody);

    if (rootNode.path("retCode").asInt() != 0) {
      throw new RuntimeException(
          "Error API Bybit (wallet-balance): " + rootNode.path("retMsg").asText());
    }

    JsonNode listNode = rootNode.path("result").path("list");
    if (listNode.isArray() && !listNode.isEmpty()) {
      JsonNode coinArray = listNode.get(0).path("coin");
      if (coinArray.isArray()) {
        for (JsonNode coinNode : coinArray) {
          AccountInfoGeneral info = new AccountInfoGeneral();
          info.setWallet(coinNode.path("coin").asText());
          info.setTotal(new BigDecimal(coinNode.path("walletBalance").asText()));
          balances.add(info);
        }
      }
    }
    return balances;
  }

  private List<AccountInfoGeneral> parseAllCoinsWallets(String jsonBody) {
    List<AccountInfoGeneral> balances = new ArrayList<>();
    JsonNode rootNode = JsonUtils.convertToJsonTree(jsonBody);

    if (rootNode.path("retCode").asInt() != 0) {
      throw new RuntimeException(
          "Error API Bybit (all-coins-balance): " + rootNode.path("retMsg").asText());
    }

    JsonNode balanceArray = rootNode.path("result").path("balance");
    if (balanceArray.isArray()) {
      for (JsonNode coinNode : balanceArray) {
        AccountInfoGeneral info = new AccountInfoGeneral();
        info.setWallet(coinNode.path("coin").asText());
        info.setTotal(new BigDecimal(coinNode.path("walletBalance").asText()));
        balances.add(info);
      }
    }
    return balances;
  }
}
