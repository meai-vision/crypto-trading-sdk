package com.meaivision.trading.base.model;

import lombok.Data;

@Data
public class ExchangeInfo {
  private String symbol;
  private int pricePrecision;
  private int quantityPrecision;
  private int baseAssetPrecision;
  private int quotePrecision;
}
