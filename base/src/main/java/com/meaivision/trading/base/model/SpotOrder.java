package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderResponse;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Generic Spot order response. Field names follow the most common REST/User-Data-Stream shape
 * across major exchanges (Binance-leaning).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SpotOrder extends OrderResponse {
  private String symbol;
  private Long orderId;
  private String clientOrderId;
  private Long transactTime;
  private BigDecimal price;
  private BigDecimal origQty;
  private BigDecimal executedQty;
  private BigDecimal cummulativeQuoteQty;
  private String status;
  private String timeInForce;
  private String type;
  private String side;
}
