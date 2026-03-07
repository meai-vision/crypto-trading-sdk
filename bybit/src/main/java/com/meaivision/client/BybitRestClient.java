package com.meaivision.client;

import okhttp3.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class BybitRestClient {

  private static final String BASE_URL = "https://api.bybit.com";
  private static final String RECV_WINDOW = "5000";

  private final String apiKey;
  private final String apiSecret;
  private final OkHttpClient client;

  public BybitRestClient(String apiKey, String apiSecret) {
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.client = new OkHttpClient();
  }

  public String sendGet(String path, Map<String, String> queryParams) throws IOException {
    Map<String, String> sortedParams = prepareParams(queryParams);
    HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + path).newBuilder();
    sortedParams.forEach(urlBuilder::addQueryParameter);
    Request request = buildRequest(urlBuilder.build(), sortedParams, "GET");
    return executeRequest(request);
  }

  public String sendPost(String path, Map<String, String> bodyParams) throws IOException {
    Map<String, String> sortedParams = prepareParams(bodyParams);
    FormBody.Builder formBuilder = new FormBody.Builder();
    sortedParams.forEach(formBuilder::add);
    Request request = buildRequest(BASE_URL + path, sortedParams, formBuilder.build());
    return executeRequest(request);
  }

  private Map<String, String> prepareParams(Map<String, String> params) {
    Map<String, String> sortedParams = new TreeMap<>();
    if (params != null) {
      sortedParams.putAll(params);
    }
    return sortedParams;
  }

  private Request buildRequest(HttpUrl url, Map<String, String> params, String method) {
    long timestamp = System.currentTimeMillis();
    String payload = timestamp + apiKey + RECV_WINDOW + buildQueryString(params);
    String signature = sign(payload);
    Request.Builder builder =
        new Request.Builder()
            .url(url)
            .addHeader("X-BAPI-API-KEY", apiKey)
            .addHeader("X-BAPI-TIMESTAMP", String.valueOf(timestamp))
            .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
            .addHeader("X-BAPI-SIGN", signature);
    if ("GET".equals(method)) {
      builder.get();
    }
    return builder.build();
  }

  private Request buildRequest(String url, Map<String, String> params, FormBody body) {
    long timestamp = System.currentTimeMillis();
    String payload = timestamp + apiKey + RECV_WINDOW + buildQueryString(params);
    String signature = sign(payload);
    return new Request.Builder()
        .url(url)
        .post(body)
        .addHeader("X-BAPI-API-KEY", apiKey)
        .addHeader("X-BAPI-TIMESTAMP", String.valueOf(timestamp))
        .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
        .addHeader("X-BAPI-SIGN", signature)
        .build();
  }

  private String executeRequest(Request request) throws IOException {
    try (Response response = client.newCall(request).execute()) {
      if (response.body() == null) {
        throw new IOException("Empty response body");
      }
      return response.body().string();
    }
  }

  private String buildQueryString(Map<String, String> params) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (sb.length() > 0) sb.append("&");
      sb.append(entry.getKey()).append("=").append(entry.getValue());
    }
    return sb.toString();
  }

  private String sign(String payload) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256Hmac.init(secretKeySpec);
      byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate Bybit signature", e);
    }
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) sb.append('0');
      sb.append(hex);
    }
    return sb.toString();
  }
}
