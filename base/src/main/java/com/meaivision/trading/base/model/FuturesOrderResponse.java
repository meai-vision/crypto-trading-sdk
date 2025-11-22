package com.meaivision.trading.base.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FuturesOrderResponse {
  private Long id;
  private String clientOrderId;
  private String cumQty;
  private String cumQuote;
  private String executedQty;
  private long orderId;
  private String avgPrice;
  private String origQty;
  private String price;
  private boolean reduceOnly;
  private String side;
  private String positionSide;
  private String status;
  private String stopPrice;
  private boolean closePosition;
  private String symbol;
  private String timeInForce;
  private String type;
  private String origType;
  private String activatePrice;
  private String priceRate;
  private long updateTime;
  private String workingType;
  private boolean priceProtect;
  private String priceMatch;
  private String selfTradePreventionMode;
  private long goodTillDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean isDeleted = false;
}
