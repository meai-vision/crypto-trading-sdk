package com.meaivision.trading.base.model;

import java.util.List;
import lombok.Data;

@Data
public class WalletHolder {
  private List<AccountInfoGeneral> wallets;
}
