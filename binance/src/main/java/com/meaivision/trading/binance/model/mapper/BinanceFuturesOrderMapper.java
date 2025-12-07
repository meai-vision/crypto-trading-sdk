package com.meaivision.trading.binance.model.mapper;

import com.meaivision.trading.base.model.FuturesOrder;
import com.meaivision.trading.binance.model.BinanceFuturesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface BinanceFuturesOrderMapper {

  @Mappings({
    @Mapping(source = "orderId", target = "id"),
    @Mapping(source = "symbol", target = "symbol"),
    @Mapping(source = "side", target = "side"),
    @Mapping(source = "origQty", target = "quantity"),
    @Mapping(source = "type", target = "type"),
    @Mapping(source = "timeInForce", target = "timeZone"),
    @Mapping(source = "updateTime", target = "updatedAt")
  })
  FuturesOrder toModel(BinanceFuturesOrder source);

  default String map(Long id) {
    return id == null ? null : id.toString();
  }
}
