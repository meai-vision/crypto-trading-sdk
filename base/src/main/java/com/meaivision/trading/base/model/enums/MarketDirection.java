package com.meaivision.trading.base.model.enums;

import lombok.Getter;

@Getter
public enum MarketDirection {
  HIGH("HIGH"),
  LOW("LOW"),
  NEUTRAL("NEUTRAL");

  private final String value;

  MarketDirection(String value) {
    this.value = value;
  }
}
