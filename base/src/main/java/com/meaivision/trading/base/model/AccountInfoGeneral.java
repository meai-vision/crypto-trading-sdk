package com.meaivision.trading.base.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AccountInfoGeneral {
  private String wallet;
  private String ticker;
  private BigDecimal total;
}
