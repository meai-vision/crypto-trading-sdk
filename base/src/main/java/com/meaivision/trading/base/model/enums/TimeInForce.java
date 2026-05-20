package com.meaivision.trading.base.model.enums;

import lombok.Getter;

@Getter
public enum TimeInForce {
  GTC("GTC"),
  IOC("IOC"),
  FOK("FOK");

  private final String stringValue;

  TimeInForce(String stringValue) {
    this.stringValue = stringValue;
  }
}
