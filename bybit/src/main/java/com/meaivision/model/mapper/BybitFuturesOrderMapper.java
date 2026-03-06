package com.meaivision.model.mapper;

import com.meaivision.model.BybitFuturesOrder;
import com.meaivision.trading.base.model.FuturesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface BybitFuturesOrderMapper {

  @Mapping(source = "orderId", target = "id")
  @Mapping(source = "symbol", target = "symbol")
  @Mapping(source = "side", target = "side")
  @Mapping(source = "qty", target = "quantity")
  @Mapping(source = "orderType", target = "type")
  @Mapping(target = "updatedAt", expression = "java(convertUpdatedTime(source.getUpdatedTime()))")
  FuturesOrder toModel(BybitFuturesOrder source);

  default Instant convertUpdatedTime(Long millis) {
    return millis != null ? Instant.ofEpochMilli(millis) : null;
  }
}
