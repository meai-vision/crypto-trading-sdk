package com.meaivision.trading.binance.service;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.model.AccountInfo;
import com.meaivision.trading.base.model.TradingClientSettings;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceAccountService implements AccountService<AccountInfo> {

  private final ObjectMapper objectMapper;
  private final ClientProvider<TradingClientSettings, FuturesClient> clientProvider;

  public BinanceAccountService(
      ObjectMapper objectMapper,
      ClientProvider<TradingClientSettings, FuturesClient> clientProvider) {
    this.objectMapper = objectMapper;
    this.clientProvider = clientProvider;
  }

  @Override
  public AccountInfo getAccountInfo(TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    UMFuturesClientImpl futuresClient = (UMFuturesClientImpl) clientProvider.get(settings);
    String response = sendRequest(futuresClient, parameters);
    return toAccountInfo(response);
  }

  private String sendRequest(
      UMFuturesClientImpl futuresClient, LinkedHashMap<String, Object> parameters) {
    try {
      String result = futuresClient.account().accountInformation(parameters);
      log.debug(result);
      return result;
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new RuntimeException("Error occurred during getting account information!", e);
    }
  }

  private AccountInfo toAccountInfo(String result) {
    try {
      JsonNode jsonNode = objectMapper.readTree(result);
      return objectMapper.convertValue(jsonNode, AccountInfo.class);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Can't process Binance response!");
    }
  }
}
