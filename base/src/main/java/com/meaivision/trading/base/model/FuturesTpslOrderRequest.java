package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FuturesTpslOrderRequest extends OrderRequest {
  private String ticker;
  private FuturesOrderRequest mainOrder;
  private FuturesOrderRequest takeProfitOrder;
  private FuturesOrderRequest stopLossOrder;
}
