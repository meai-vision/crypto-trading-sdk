package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.TradingClientSettings;

public interface AccountService<T> {

  T getAccountInfo(TradingClientSettings settings);
}
