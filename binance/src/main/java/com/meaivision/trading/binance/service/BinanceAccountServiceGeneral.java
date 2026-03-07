package com.meaivision.trading.binance.service;

import com.binance.connector.client.SpotClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.AccountInfoGeneral;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.exception.BinanceException;
import com.meaivision.trading.binance.model.BinanceAccountInfoGeneral;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class BinanceAccountServiceGeneral implements AccountService<BinanceAccountInfoGeneral> {

  private static final String DEFAULT_BINANCE_TICKER = "BTC";

  private final ClientProvider<TradingClientSettings, SpotClient> clientProvider;

  public BinanceAccountServiceGeneral(
      ClientProvider<TradingClientSettings, SpotClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public BinanceAccountInfoGeneral getAccountInfo(TradingClientSettings settings) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
    SpotClient spotClient = clientProvider.get(settings);
    String response = sendRequest(spotClient, parameters);
    return toWallets(response);
  }

  private String sendRequest(SpotClient spotClient, LinkedHashMap<String, Object> parameters) {
    return spotClient.createWallet().walletBalance(parameters);
  }

  private BinanceAccountInfoGeneral toWallets(String result) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(result);
    List<AccountInfoGeneral> wallets =
        StreamSupport.stream(jsonNode.spliterator(), false)
            .map(
                walletNode -> {
                  JsonNode activate = walletNode.get("activate");
                  if (activate == null) {
                    throw new BinanceException(
                        "Can't find 'activate' parameter in account response for general!");
                  }

                  if (!activate.booleanValue()) {
                    return null;
                  }

                  AccountInfoGeneral accountInfoGeneral = new AccountInfoGeneral();
                  JsonNode walletNameNode = walletNode.get("walletName");
                  if (walletNameNode == null) {
                    throw new BinanceException(
                        "Can't find name of wallet in account response for general!");
                  }
                  String name = walletNameNode.textValue();
                  accountInfoGeneral.setWallet(name);
                  accountInfoGeneral.setTicker(DEFAULT_BINANCE_TICKER);
                  JsonNode balanceNode = walletNode.get("balance");
                  if (balanceNode == null) {
                    throw new BinanceException(
                        "Can't find amount of wallet '%s' in account response for general!"
                            .formatted(name));
                  }
                  String amount = balanceNode.textValue();
                  accountInfoGeneral.setTotal(new BigDecimal(amount));
                  return accountInfoGeneral;
                })
            .filter(Objects::nonNull)
            .toList();
    BinanceAccountInfoGeneral binanceAccountInfoGeneral = new BinanceAccountInfoGeneral();
    binanceAccountInfoGeneral.setWallets(wallets);
    return binanceAccountInfoGeneral;
  }
}
