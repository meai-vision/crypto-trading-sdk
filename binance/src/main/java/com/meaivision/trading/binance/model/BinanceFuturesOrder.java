package com.meaivision.trading.binance.model;

import java.time.Instant;
import lombok.Data;

@Data
public class BinanceFuturesOrder {
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
  private Instant updateTime;
  private String workingType;
  private boolean priceProtect;
  private String priceMatch;
  private String selfTradePreventionMode;
  private long goodTillDate;
}
