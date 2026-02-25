package com.meaivision.model.mapper;

import com.meaivision.model.BybitSpotOrder;
import com.meaivision.trading.base.model.SpotOrder;
import org.mapstruct.Mapper;

@Mapper
public interface BybitSpotOrderMapper {

    SpotOrder toModel(BybitSpotOrder bybitSpotOrder);
}