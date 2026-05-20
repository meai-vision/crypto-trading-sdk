package com.meaivision.trading.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Data;

/** Raw Binance spot order DTO matching <code>POST /api/v3/order</code> JSON shape. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceSpotOrder {
  private String symbol;
  private Long orderId;
  private Long orderListId;
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
  private BigDecimal stopPrice;
  private Long workingTime;
  private String selfTradePreventionMode;
}
