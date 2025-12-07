package com.meaivision.trading.base.exception;

public class TradingException extends RuntimeException {
  public TradingException(String message) {
    super(message);
  }

  public TradingException(String message, Throwable cause) {
    super(message, cause);
  }
}
