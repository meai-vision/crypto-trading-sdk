package com.meaivision.trading.okx.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.AccountInfoGeneral;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.model.WalletHolder;
import com.meaivision.trading.base.model.enums.WalletType;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.okx.exception.OkxException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OkxAccountServiceGeneral implements AccountService<WalletHolder> {

  private static final String BASE_URL = "https://www.okx.com";
  private static final String TRADING_BALANCE_URL = "/api/v5/account/balance";
  private static final String FUNDING_BALANCE_URL = "/api/v5/asset/balances";

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Override
  public WalletHolder getAccountInfo(TradingClientSettings settings) {
    List<AccountInfoGeneral> allBalances =
        Stream.of(
                Map.entry(WalletType.TRADING.name(), TRADING_BALANCE_URL),
                Map.entry(WalletType.FUND.name(), FUNDING_BALANCE_URL))
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
    String passphrase = settings.getPassphrase();
    String timestamp = TIME_FORMATTER.format(Instant.now());
    String payloadToSign = timestamp + "GET" + endpoint;
    String signature = calculateSignature(payloadToSign, apiSecret);

    HttpRequest request =
        HttpRequest.newBuilder()
            .timeout(Duration.of(30, ChronoUnit.SECONDS))
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json")
            .header("OK-ACCESS-KEY", apiKey)
            .header("OK-ACCESS-SIGN", signature)
            .header("OK-ACCESS-TIMESTAMP", timestamp)
            .header("OK-ACCESS-PASSPHRASE", passphrase)
            .GET()
            .build();

    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (Exception e) {
      throw new OkxException("Failed to fetch OKX balance for endpoint: " + endpoint, e);
    }
  }

  private String calculateSignature(String payload, String apiSecret) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKey =
          new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256Hmac.init(secretKey);
      byte[] macData = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

      return Base64.getEncoder().encodeToString(macData);
    } catch (Exception e) {
      throw new OkxException("Failed to calculate OKX signature", e);
    }
  }

  private List<AccountInfoGeneral> parseBalances(String walletName, String responseBody) {
    JsonNode rootNode = JsonUtils.convertToJsonTree(responseBody);

    if (rootNode == null) {
      throw new OkxException(
          "Received empty or invalid JSON from OKX API for wallet: " + walletName);
    }

    if (rootNode.has("code") && rootNode.get("code").asInt() != 0) {
      String errorMsg = rootNode.has("msg") ? rootNode.get("msg").asText() : "Unknown error";
      throw new OkxException("API Error (OKX): " + errorMsg);
    }

    List<AccountInfoGeneral> balances = new ArrayList<>();
    JsonNode dataNode = rootNode.path("data");

    if (dataNode.isArray() && !dataNode.isEmpty()) {
      if (WalletType.TRADING.name().equals(walletName)) {
        JsonNode detailsNode = dataNode.get(0).path("details");
        if (detailsNode.isArray()) {
          for (JsonNode coinNode : detailsNode) {
            String ticker = coinNode.path("ccy").asText();
            BigDecimal amount = new BigDecimal(coinNode.path("eq").asText("0"));

            if (amount.compareTo(BigDecimal.ZERO) > 0) {
              balances.add(createAccountInfo(walletName, ticker, amount));
            }
          }
        }
      } else if (WalletType.FUND.name().equals(walletName)) {
        for (JsonNode coinNode : dataNode) {
          String ticker = coinNode.path("ccy").asText();
          BigDecimal amount = new BigDecimal(coinNode.path("bal").asText("0"));

          if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balances.add(createAccountInfo(walletName, ticker, amount));
          }
        }
      }
    }

    return balances;
  }

  private AccountInfoGeneral createAccountInfo(String wallet, String ticker, BigDecimal amount) {
    AccountInfoGeneral info = new AccountInfoGeneral();
    info.setWallet(wallet);
    info.setTicker(ticker);
    info.setTotal(amount);
    return info;
  }
}
