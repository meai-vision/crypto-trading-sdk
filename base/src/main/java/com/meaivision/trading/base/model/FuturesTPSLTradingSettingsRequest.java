package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// TODO: create request without specifying particular ticker and period.
//  Make requests to create by default flow
public class FuturesTPSLTradingSettingsRequest {
  private String tickerId;
  private String periodId;
  private String userTradingSettingsId;
  private int leverage;
  private BigDecimal firstPriceChangeIndex;
  private BigDecimal secondPriceChangeIndex;
  private BigDecimal balanceIndexPerOrder;
}
