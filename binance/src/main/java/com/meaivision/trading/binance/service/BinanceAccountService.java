package com.meaivision.trading.binance.service;

import com.binance.connector.futures.client.FuturesClient;
import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.AccountInfo;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.exception.BinanceException;
import com.meaivision.trading.binance.model.BinanceAccountInfo;
import com.meaivision.trading.binance.model.mapper.BinanceAccountInfoMapper;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinanceAccountService implements AccountService<AccountInfo> {

  private final BinanceAccountInfoMapper binanceAccountInfoMapper;
  private final ClientProvider<TradingClientSettings, FuturesClient> clientProvider;

  public BinanceAccountService(
      BinanceAccountInfoMapper binanceAccountInfoMapper,
      ClientProvider<TradingClientSettings, FuturesClient> clientProvider) {
    this.binanceAccountInfoMapper = binanceAccountInfoMapper;
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
      log.debug("Account information: {}", result);
      return result;
    } catch (BinanceConnectorException | BinanceClientException e) {
      throw new BinanceException("Error occurred during getting account information!", e);
    }
  }

  private AccountInfo toAccountInfo(String result) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
    BinanceAccountInfo binanceAccountInfo =
        JsonUtils.convertToObject(jsonNode, BinanceAccountInfo.class);
    return binanceAccountInfoMapper.toModel(binanceAccountInfo);
  }
}
