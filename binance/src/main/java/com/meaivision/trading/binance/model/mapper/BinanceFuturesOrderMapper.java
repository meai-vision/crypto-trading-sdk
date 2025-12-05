package com.meaivision.trading.binance.model.mapper;

import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.binance.model.BinanceFuturesOrder;
import org.mapstruct.Mapper;

@Mapper
public interface BinanceFuturesOrderMapper {

  FuturesOrder toObject(BinanceFuturesOrder binanceFuturesOrder);
}
