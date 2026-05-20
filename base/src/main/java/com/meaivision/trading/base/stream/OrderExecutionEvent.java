package com.meaivision.trading.base.stream;

import lombok.Builder;
import lombok.Value;

/**
 * Order execution-report event from the User Data Stream (Binance: {@code executionReport}). Cross-
 * exchange-normalized; status is the raw string from the exchange ({@code NEW}, {@code FILLED},
 * {@code PARTIALLY_FILLED}, {@code CANCELED}, {@code REJECTED}, {@code EXPIRED}, ...). Consumers
 * may map this to {@link com.meaivision.trading.base.model.enums.OrderStatus}.
 */
@Value
@Builder
public class OrderExecutionEvent implements UserDataEvent {
  long eventTimeMs;
  long transactionTimeMs;
  long receivedNanos;

  String symbol;
  long orderId;
  String clientOrderId;
  String originalClientOrderId;
  String side;
  String orderType;
  String timeInForce;
  String status;
  String executionType;

  double orderPrice;
  double orderQty;
  double lastFilledQty;
  double cumulativeFilledQty;
  double lastFilledPrice;
  double commission;
  String commissionAsset;
}
