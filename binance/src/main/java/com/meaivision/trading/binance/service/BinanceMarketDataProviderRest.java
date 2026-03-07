package com.meaivision.trading.binance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.TickerPrice;
import com.meaivision.trading.base.service.MarketDataProvider;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.exception.BinanceException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BinanceMarketDataProviderRest implements MarketDataProvider {

  private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/price";

  private final HttpClient httpClient;

  public BinanceMarketDataProviderRest() {
    this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
  }

  @Override
  public TickerPrice getCurrentPrice(String ticker) {
    try {
      String url = BINANCE_API_URL + "?symbol=" + ticker.toUpperCase();

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(url))
              .GET()
              .timeout(Duration.ofSeconds(5))
              .header("Content-Type", "application/json")
              .build();

      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new BinanceException(
            "Binance API Error: Code " + response.statusCode() + ", Body: " + response.body());
      }
      JsonNode bodyNode = JsonUtils.convertToJsonTree(response.body());
      JsonNode priceNode = bodyNode.get("price");

      if (priceNode == null) {
        throw new BinanceException("Invalid response from Binance: 'price' field is missing");
      }

      return TickerPrice.of(ticker, new BigDecimal(priceNode.asText()));

    } catch (IOException | InterruptedException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw new BinanceException("Failed to fetch price from Binance for " + ticker, e);
    }
  }
}
