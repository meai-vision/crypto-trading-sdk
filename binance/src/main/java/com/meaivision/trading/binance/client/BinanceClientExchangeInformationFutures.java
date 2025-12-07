package com.meaivision.trading.binance.client;

import com.binance.connector.futures.client.enums.DefaultUrls;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BinanceClientExchangeInformationFutures {

  public String getExchangeInformation() {
    String url = DefaultUrls.USDM_PROD_URL + "/fapi/v1/exchangeInfo";
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
