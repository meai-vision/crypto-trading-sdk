package com.meaivision.trading.binance.exception;

import com.meaivision.trading.base.exception.TradingException;

public class BinanceException extends TradingException {
  public BinanceException(String message) {
    super(message);
  }

  public BinanceException(String message, Throwable cause) {
    super(message, cause);
  }
}
