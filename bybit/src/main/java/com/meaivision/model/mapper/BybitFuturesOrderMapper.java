package com.meaivision.model.mapper;

import com.meaivision.model.BybitFuturesOrder;
import com.meaivision.trading.base.model.FuturesOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface BybitFuturesOrderMapper {

    @Mappings({
            @Mapping(source = "orderId", target = "id"),
            @Mapping(source = "symbol", target = "symbol"),
            @Mapping(source = "side", target = "side"),
            @Mapping(source = "qty", target = "quantity"),
            @Mapping(source = "orderType", target = "type"),
            @Mapping(source = "timeInForce", target = "timeZone"),
            @Mapping(source = "updatedTime", target = "updatedAt")
    })
    FuturesOrder toModel(BybitFuturesOrder source);

    default String map(Long id) {
        return id == null ? null : id.toString();
    }
}