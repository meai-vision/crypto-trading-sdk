package com.meaivision.exception;

import com.meaivision.trading.base.exception.TradingException;

public class BybitException extends TradingException {
    public BybitException(String message) {
        super(message);
    }

    public BybitException(String message, Throwable cause) {
        super(message, cause);
    }
}
