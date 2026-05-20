package com.meaivision.trading.base.stream;

import com.meaivision.trading.base.exception.TradingException;

public class StreamException extends TradingException {
  public StreamException(String message) {
    super(message);
  }

  public StreamException(String message, Throwable cause) {
    super(message, cause);
  }
}
