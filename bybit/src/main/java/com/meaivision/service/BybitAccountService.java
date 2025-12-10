package com.meaivision.service;

import com.bybit.api.client.restApi.BybitApiAsyncTradeRestClient;
import com.meaivision.model.BybitAccountInfo;
import com.meaivision.model.mapper.BybitAccountInfoMapper;
import com.meaivision.trading.base.model.AccountInfo;
import com.meaivision.trading.base.model.TradingClientSettings;
import com.meaivision.trading.base.service.AccountService;
import com.meaivision.trading.base.service.ClientProvider;
import com.meaivision.trading.base.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class BybitAccountService implements AccountService<AccountInfo> {

    private final BybitAccountInfoMapper mapper;
    private final ClientProvider<TradingClientSettings, BybitApiAsyncTradeRestClient> clientProvider;

    public BybitAccountService(
            BybitAccountInfoMapper mapper,
            ClientProvider<TradingClientSettings, BybitApiAsyncTradeRestClient> clientProvider
    ) {
        this.mapper = mapper;
        this.clientProvider = clientProvider;
    }

    @Override
    public AccountInfo getAccountInfo(TradingClientSettings settings) {
        BybitApiAsyncTradeRestClient client = clientProvider.get(settings);
        String response = sendRequest(client);
        return toAccountInfo(response);
    }

    private String sendRequest(BybitApiAsyncTradeRestClient client) {
        /*
        ..................................................
        :                    ......                      :
        :                 .:||||||||:.                   :
        :                /            \                  :
        :               (   o      o   )                 :
        :-------@@@@----------:  :----------@@@@---------:
        :                     `--'                       :
        :                WORK IN PROGRESS                :
        :................................................:
         */
        return "";
    }

    private AccountInfo toAccountInfo(String response) {
        BybitAccountInfo info = JsonUtils.convertToObject(JsonUtils.convertToJsonTree(response), BybitAccountInfo.class);
        return mapper.toModel(info);
    }
}