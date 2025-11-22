package com.meaivision.trading.base.service;

import com.meaivision.trading.base.model.ExchangeInfo;
import java.util.List;

public interface ExchangeInformationServiceFutures {
  List<ExchangeInfo> getSymbolsInformation();
}
