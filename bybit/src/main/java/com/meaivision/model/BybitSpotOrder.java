package com.meaivision.model;

import java.time.Instant;
import lombok.Data;

@Data
public class BybitSpotOrder {

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

    private String orderStatus;
    private String timeInForce;

    private Instant createdTime;
    private Instant updatedTime;
}