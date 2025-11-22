package com.meaivision.trading.base.model;

import lombok.Data;

@Data
public class FuturesTpslOrder {
  private String ticker;
  private FuturesOrder mainOrder;
  private FuturesOrder takeProfitOrder;
  private FuturesOrder stopLossOrder;
}
