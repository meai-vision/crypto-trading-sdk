package com.meaivision.trading.base.model.enums;

import lombok.Getter;

@Getter
public enum OrderType {
  MARKET("MARKET"),
  LIMIT("LIMIT"),
  LIMIT_MAKER("LIMIT_MAKER"),
  STOP_LOSS("STOP_LOSS"),
  STOP_LOSS_LIMIT("STOP_LOSS_LIMIT"),
  TAKE_PROFIT("TAKE_PROFIT"),
  TAKE_PROFIT_LIMIT("TAKE_PROFIT_LIMIT"),
  STOP_MARKET("STOP_MARKET"),
  TAKE_PROFIT_MARKET("TAKE_PROFIT_MARKET");

  private final String stringValue;

  OrderType(String stringValue) {
    this.stringValue = stringValue;
  }
}
