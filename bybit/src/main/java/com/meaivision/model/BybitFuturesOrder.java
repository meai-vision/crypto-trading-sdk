package com.meaivision.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitFuturesOrder {

  @JsonProperty("orderId")
  private String orderId;

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("side")
  private String side;

  @JsonProperty("qty")
  private String qty;

  @JsonProperty("orderType")
  private String orderType;

  @JsonProperty("updatedTime")
  private Long updatedTime;
}
