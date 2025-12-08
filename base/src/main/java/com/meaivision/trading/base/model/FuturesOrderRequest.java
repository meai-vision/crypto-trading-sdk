package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderRequest;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FuturesOrderRequest extends OrderRequest {
  private String symbol;
  private String side;
  private String type;
  private BigDecimal price;
  private BigDecimal stopPrice;
  private BigDecimal quantity;
}
