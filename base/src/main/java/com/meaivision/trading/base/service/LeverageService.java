package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.TradingClientSettings;

public interface LeverageService<T, R> {
  R changeInitialLeverage(T request, TradingClientSettings settings);
}
