package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskValues {
  private int leverage;
  private BigDecimal firstPriceChangeIndex;
  private BigDecimal secondPriceChangeIndex;
  private BigDecimal balanceIndexPerOrder;
}
