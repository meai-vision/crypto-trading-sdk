package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.fundamental.OrderRequest;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Spot order request. Only {@code symbol}, {@code side} and {@code type} are mandatory; the
 * remaining fields are optional and will be forwarded to the exchange only when non-null. This lets
 * the same DTO model MARKET, LIMIT, LIMIT_MAKER (post-only) and stop-style orders without
 * subclassing.
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class SpotOrderRequest extends OrderRequest {
  private String symbol;
  private String side;
  private String type;
  private BigDecimal quantity;
  private BigDecimal quoteOrderQty;
  private BigDecimal price;
  private BigDecimal stopPrice;
  private String timeInForce;
  private String newClientOrderId;
  private String newOrderRespType;
}
