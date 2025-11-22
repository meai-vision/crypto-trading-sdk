package com.meaivision.trading.binance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.trading.base.model.ExchangeInfo;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.service.ExchangeInformationServiceFutures;
import com.meaivision.trading.base.util.JsonUtils;
import com.meaivision.trading.binance.client.ClientExchangeInformationFutures;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class BinanceExchangeInformationServiceFutures implements ExchangeInformationServiceFutures {

  private final ClientProvider<Optional<Object>, ClientExchangeInformationFutures> clientProvider;

  public BinanceExchangeInformationServiceFutures(
      ClientProvider<Optional<Object>, ClientExchangeInformationFutures> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public List<ExchangeInfo> getSymbolsInformation() {
    ClientExchangeInformationFutures client = clientProvider.get(Optional.empty());
    String response = client.getExchangeInformation();
    JsonNode symbolsNode = getSymbols(response);
    if (symbolsNode == null) {
      throw new RuntimeException("Can't find exchange information for symbols!");
    }
    return StreamSupport.stream(symbolsNode.spliterator(), true)
        .map(node -> JsonUtils.convertToObject(node, ExchangeInfo.class))
        .toList();
  }

  private JsonNode getSymbols(String exchangeInfoJson) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(exchangeInfoJson);
    return jsonNode.get("symbols");
  }
}
