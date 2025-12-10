package com.meaivision.model.mapper;

import com.meaivision.model.BybitAccountInfo;
import com.meaivision.trading.base.model.AccountInfo;
import org.mapstruct.Mapper;

@Mapper
public interface BybitAccountInfoMapper {
    AccountInfo toModel(BybitAccountInfo binanceAccountInfo);
}

