package com.meaivision.trading.binance.model.mapper;

import com.meaivision.trading.base.model.AccountInfo;
import com.meaivision.trading.binance.model.BinanceAccountInfo;
import org.mapstruct.Mapper;

@Mapper
public interface BinanceAccountInfoMapper {
  AccountInfo toModel(BinanceAccountInfo binanceAccountInfo);
}
