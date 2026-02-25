package com.meaivision.model;

import java.time.Instant;
import lombok.Data;

@Data
public class BybitFuturesOrder {

    private String orderId;
    private String orderLinkId;

    private String symbol;
    private String side;
    private String orderType;

    private String price;
    private String qty;
    private String avgPrice;
    private String cumExecQty;
    private String cumExecValue;

    private String stopOrderType;
    private String triggerPrice;
    private String triggerDirection;

    private String timeInForce;
    private String orderStatus;

    private Boolean reduceOnly;
    private Boolean closeOnTrigger;

    private Instant createdTime;
    private Instant updatedTime;
}