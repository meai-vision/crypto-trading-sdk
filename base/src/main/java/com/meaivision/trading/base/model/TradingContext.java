package com.meaivision.trading.base.model;

import com.meaivision.trading.base.model.enums.MarketDirection;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradingContext {
  private String ticker;
  private BigDecimal price;
  private MarketDirection marketDirection;
  private RiskValues riskValues;
  private int quantityPrecision;
}
