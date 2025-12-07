package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderResponse;
import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FuturesOrder extends OrderResponse {
  private String id;
  private String symbol;
  private String side;
  private String quantity;
  private String type;
  private String timeZone;
  private Instant updatedAt;
}
