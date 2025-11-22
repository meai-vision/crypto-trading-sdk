package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
public class AccountInfo {
  private BigDecimal availableBalance;
  private BigDecimal totalWalletBalance;
  private BigDecimal totalMarginBalance;
  private BigDecimal totalCrossWalletBalance;
}
