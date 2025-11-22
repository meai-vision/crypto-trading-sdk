package com.meaivision.trading.binance.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ClientExchangeInformationFutures {

  private static final String BINANCE_FUTURES_HOST = "https://fapi.binance.com";

  public String getExchangeInformation() {
    String url = BINANCE_FUTURES_HOST + "/fapi/v1/exchangeInfo";
    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Error occurred during sending a request into " + url, e);
    }
  }
}
