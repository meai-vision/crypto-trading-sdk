package com.meaivision.trading.base.model;

import lombok.Data;

@Data
public class LeverageRequest {
  private String symbol;
  private double leverage;
}
