package com.meaivision.model;

import com.meaivision.trading.base.model.AccountInfoGeneral;
import java.util.List;
import lombok.Data;

@Data
public class BybitAccountInfoGeneral {

    private List<AccountInfoGeneral> wallets;
}