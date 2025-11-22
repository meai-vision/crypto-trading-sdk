package com.meaivision.trading.base.model.enums;

import lombok.Getter;

@Getter
public enum OrderType {
  MARKET("MARKET"),
  STOP_MARKET("STOP_MARKET"),
  TAKE_PROFIT_MARKET("TAKE_PROFIT_MARKET");

  private final String stringValue;

  OrderType(String stringValue) {
    this.stringValue = stringValue;
  }
}
