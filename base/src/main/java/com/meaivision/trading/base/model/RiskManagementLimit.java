package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class RiskManagementLimit {
  private String tickerName;
  private BigDecimal lowestValue;
  private BigDecimal highestValue;
  private BigDecimal orderTotal;
}
