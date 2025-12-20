package com.meaivision.trading.binance.model;

import com.meaivision.trading.base.model.AccountInfoGeneral;
import java.util.List;
import lombok.Data;

@Data
public class BinanceAccountInfoGeneral {
  List<AccountInfoGeneral> wallets;
}
