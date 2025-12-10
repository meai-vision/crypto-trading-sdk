package com.meaivision.client;

import com.meaivision.trading.base.service.ClientProvider;
import java.util.Optional;

public class BybitClientProviderExchangeInformationFutures
        implements ClientProvider<Optional<Object>, BybitClientExchangeInformationFutures> {

    @Override
    public BybitClientExchangeInformationFutures get(Optional<Object> properties) {
        return new BybitClientExchangeInformationFutures();
    }
}

