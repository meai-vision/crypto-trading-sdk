package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class SpotOrderRequest extends OrderRequest {
  private String symbol;
  private String side;
  private String type;
}
