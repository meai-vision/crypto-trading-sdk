package com.meaivision.trading.binance.service;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.meaivision.trading.base.model.LeverageRequest;
import com.meaivision.trading.base.model.LeverageResponse;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.LeverageService;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceLeverageService implements LeverageService<LeverageRequest, LeverageResponse> {

  private final ClientProvider<TradingClientSettings, FuturesClient> clientProvider;

  public BinanceLeverageService(
      ClientProvider<TradingClientSettings, FuturesClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public LeverageResponse changeInitialLeverage(
      LeverageRequest request, TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    FuturesClient futuresClient = clientProvider.get(settings);
    parameters.put("symbol", request.getSymbol());
    parameters.put("leverage", request.getLeverage());
    String string = sendRequest(request, futuresClient, parameters);
    return new LeverageResponse();
  }

  private String sendRequest(
      LeverageRequest request,
      FuturesClient futuresClient,
      LinkedHashMap<String, Object> parameters) {
    try {
      String result = futuresClient.account().changeInitialLeverage(parameters);
      log.debug(result);
      return result;
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new RuntimeException(
          "Error occurred during changing initial leverage for " + request.getSymbol(), e);
    }
  }
}
