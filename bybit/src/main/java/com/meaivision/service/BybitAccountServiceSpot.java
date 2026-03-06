package com.meaivision.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.meaivision.BybitConstants;
import com.meaivision.client.BybitRestClient;
import com.meaivision.exception.BybitException;
import com.meaivision.model.BybitAccountInfoGeneral;
import com.meaivision.trading.base.model.AccountInfoGeneral;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
public class BybitAccountServiceSpot implements AccountService<BybitAccountInfoGeneral> {

  private final ClientProvider<TradingClientSettings, BybitRestClient> clientProvider;

  public BybitAccountServiceSpot(
      ClientProvider<TradingClientSettings, BybitRestClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public BybitAccountInfoGeneral getAccountInfo(TradingClientSettings settings) {
    BybitRestClient client = clientProvider.get(settings);
    Map<String, String> params = new LinkedHashMap<>();
    params.put(BybitConstants.PARAM_ACCOUNT_TYPE, BybitConstants.ACCOUNT_TYPE_UNIFIED);
    String response = sendRequest(client, params);
    return parseWallets(response);
  }

  private String sendRequest(BybitRestClient client, Map<String, String> params) {
    try {
      String result = client.sendGet("/v5/account/wallet-balance", params);
      log.debug("Bybit spot wallet response: {}", result);
      return result;
    } catch (IOException e) {
      throw new BybitException("Error occurred during getting Bybit spot wallet balance", e);
    }
  }

  private BybitAccountInfoGeneral parseWallets(String response) {
    JsonNode jsonNode = JsonUtils.convertToJsonTree(response);

    JsonNode coinsNode =
        jsonNode
            .path(BybitConstants.FIELD_RESULT)
            .path(BybitConstants.FIELD_LIST)
            .get(0)
            .path(BybitConstants.FIELD_COIN_NAME);

    if (coinsNode.isMissingNode()) {
      throw new BybitException("Can't find coin list in Bybit response");
    }

    List<AccountInfoGeneral> wallets =
        StreamSupport.stream(coinsNode.spliterator(), false)
            .map(this::mapCoinNode)
            .filter(Objects::nonNull)
            .toList();

    BybitAccountInfoGeneral result = new BybitAccountInfoGeneral();
    result.setWallets(wallets);

    return result;
  }

  private AccountInfoGeneral mapCoinNode(JsonNode coinNode) {
    String coin = coinNode.path(BybitConstants.FIELD_COIN_NAME).asText(null);
    if (coin == null) {
      throw new BybitException("Can't find coin name in Bybit response");
    }

    BigDecimal total =
        new BigDecimal(coinNode.path(BybitConstants.FIELD_WALLET_BALANCE).asText("0"));
    if (total.compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }

    AccountInfoGeneral accountInfo = new AccountInfoGeneral();
    accountInfo.setWallet(coin);
    accountInfo.setTotal(total);

    return accountInfo;
  }
}
