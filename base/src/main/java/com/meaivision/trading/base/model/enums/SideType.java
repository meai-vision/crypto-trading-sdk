package com.meaivision.trading.base.model.enums;

import lombok.Getter;

@Getter
public enum SideType {
  SELL("SELL"),
  BUY("BUY"),
  NEUTRAL("NEUTRAL");

  private final String stringValue;

  SideType(String stringValue) {
    this.stringValue = stringValue;
  }
}
