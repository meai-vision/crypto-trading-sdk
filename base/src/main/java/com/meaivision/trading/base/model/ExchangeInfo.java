package com.meaivision.trading.base.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeInfo {
  private String symbol;
  private int pricePrecision;
  private int quantityPrecision;
  private int baseAssetPrecision;
  private int quotePrecision;
}
