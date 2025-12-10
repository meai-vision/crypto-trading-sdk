package com.meaivision.client;

import com.bybit.api.client.restApi.BybitApiAsyncTradeRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.ClientProvider;

public class BybitClientProvider
        implements ClientProvider<TradingClientSettings, BybitApiAsyncTradeRestClient> {

    @Override
    public BybitApiAsyncTradeRestClient get(TradingClientSettings properties) {

        BybitApiClientFactory factory = BybitApiClientFactory.newInstance(
                properties.getApiKey(),
                properties.getSecretKey()
        );

        return factory.newAsyncTradeRestClient();
    }
}
