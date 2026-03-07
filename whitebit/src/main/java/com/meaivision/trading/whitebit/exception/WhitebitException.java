package com.meaivision.trading.whitebit.exception;

import com.meaivision.trading.base.exception.TradingException;

public class WhitebitException extends TradingException {
  public WhitebitException(String message) {
    super(message);
  }

  public WhitebitException(String message, Throwable cause) {
    super(message, cause);
  }
}
