package com.meaivision.trading.okx.exception;

import com.meaivision.trading.base.exception.TradingException;

public class OkxException extends TradingException {
  public OkxException(String message) {
    super(message);
  }

  public OkxException(String message, Throwable cause) {
    super(message, cause);
  }
}
