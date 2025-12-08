package com.meaivision.trading.binance.model.mapper;

import com.meaivision.trading.base.model.SpotOrder;
import com.meaivision.trading.binance.model.BinanceSpotOrder;
import org.mapstruct.Mapper;

@Mapper
public interface BinanceSpotOrderMapper {
  SpotOrder toModel(BinanceSpotOrder binanceSpotOrder);
}
