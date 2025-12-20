package com.meaivision.trading.binance.client;

import com.binance.connector.client.common.configuration.ClientConfiguration;
import com.binance.connector.client.common.configuration.SignatureConfiguration;
import com.binance.connector.client.wallet.rest.WalletRestApiUtil;
import com.binance.connector.client.wallet.rest.api.CapitalApi;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;

public class BinanceClientProviderWalletCapital
    implements ClientProvider<TradingClientSettings, CapitalApi> {

  @Override
  public CapitalApi get(TradingClientSettings properties) {
    SignatureConfiguration signatureConfiguration = new SignatureConfiguration();
    signatureConfiguration.setApiKey(properties.getApiKey());
    signatureConfiguration.setSecretKey(properties.getSecretKey());
    ClientConfiguration clientConfiguration = WalletRestApiUtil.getClientConfiguration();
    clientConfiguration.setSignatureConfiguration(signatureConfiguration);
    return new CapitalApi(clientConfiguration);
  }
}
