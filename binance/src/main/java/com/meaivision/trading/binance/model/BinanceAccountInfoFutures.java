package com.meaivision.trading.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.meaivision.trading.base.model.AccountInfoFutures;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAccountInfoFutures extends AccountInfoFutures {}
