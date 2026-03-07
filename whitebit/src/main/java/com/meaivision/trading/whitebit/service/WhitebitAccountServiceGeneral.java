package com.meaivision.trading.whitebit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.AccountInfoGeneral;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.WalletHolder;
import com.meaivision.trading.base.model.enums.WalletType;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.whitebit.exception.WhitebitException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class WhitebitAccountServiceGeneral implements AccountService<WalletHolder> {

  private static final String BASE_URL = "https://whitebit.com";
  private static final String MAIN_BALANCE_URL = "/api/v4/main-account/balance";
  private static final String COLLATERAL_BALANCE_URL = "/api/v4/collateral-account/balance";

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Override
  public WalletHolder getAccountInfo(TradingClientSettings settings) {
    List<AccountInfoGeneral> allBalances =
        Stream.of(
                Map.entry(WalletType.UNIFIED.name(), MAIN_BALANCE_URL),
                Map.entry(WalletType.COLLATERAL.name(), COLLATERAL_BALANCE_URL))
            .flatMap(
                entry -> {
                  String walletName = entry.getKey();
                  String endpoint = entry.getValue();
                  String responseBody = sendRequest(endpoint, settings);
                  return parseBalances(walletName, responseBody).stream();
                })
            .toList();

    WalletHolder accountInfo = new WalletHolder();
    accountInfo.setWallets(allBalances);
    return accountInfo;
  }

  private String sendRequest(String endpoint, TradingClientSettings settings) {
    String apiKey = settings.getApiKey();
    String apiSecret = settings.getSecretKey();
    String nonce = String.valueOf(System.currentTimeMillis());

    String jsonBody = "{\"request\":\"" + endpoint + "\",\"nonce\":\"" + nonce + "\"}";

    String payloadBase64 =
        Base64.getEncoder().encodeToString(jsonBody.getBytes(StandardCharsets.UTF_8));

    String signature = calculateSignature(payloadBase64, apiSecret);

    HttpRequest request =
        HttpRequest.newBuilder()
            .timeout(Duration.of(30, ChronoUnit.SECONDS))
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json")
            .header("X-TXC-APIKEY", apiKey)
            .header("X-TXC-PAYLOAD", payloadBase64)
            .header("X-TXC-SIGNATURE", signature)
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (Exception e) {
      throw new WhitebitException("Failed to fetch WhiteBIT balance for endpoint: " + endpoint, e);
    }
  }

  private String calculateSignature(String payloadBase64, String apiSecret) {
    try {
      Mac sha512Hmac = Mac.getInstance("HmacSHA512");
      SecretKeySpec secretKey =
          new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
      sha512Hmac.init(secretKey);
      byte[] macData = sha512Hmac.doFinal(payloadBase64.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(macData);
    } catch (Exception e) {
      throw new WhitebitException("Failed to calculate WhiteBIT signature", e);
    }
  }

  private List<AccountInfoGeneral> parseBalances(String walletName, String responseBody) {
    JsonNode rootNode = JsonUtils.convertToJsonTree(responseBody);

    if (rootNode == null) {
      throw new WhitebitException(
          "Received empty or invalid JSON from WhiteBIT API for wallet: " + walletName);
    }

    if (rootNode.has("code") && rootNode.get("code").asInt() != 0) {
      String errorMsg =
          rootNode.has("message") ? rootNode.get("message").asText() : "Unknown error";
      throw new WhitebitException("API Error (WhiteBIT): " + errorMsg);
    }

    List<AccountInfoGeneral> balances = new ArrayList<>();
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> field = fields.next();
      String ticker = field.getKey();
      JsonNode balanceInfoNode = field.getValue();

      if (ticker.equals("code") || ticker.equals("message") || ticker.equals("errors")) {
        continue;
      }

      BigDecimal amount = BigDecimal.ZERO;
      if (balanceInfoNode.isObject() && balanceInfoNode.has("main_balance")) {
        amount = new BigDecimal(balanceInfoNode.get("main_balance").asText());
      } else if (balanceInfoNode.isTextual() || balanceInfoNode.isNumber()) {
        amount = new BigDecimal(balanceInfoNode.asText());
      }

      if (amount.compareTo(BigDecimal.ZERO) > 0) {
        AccountInfoGeneral info = new AccountInfoGeneral();
        info.setWallet(walletName);
        info.setTicker(ticker);
        info.setTotal(amount);
        balances.add(info);
      }
    }

    return balances;
  }
}
