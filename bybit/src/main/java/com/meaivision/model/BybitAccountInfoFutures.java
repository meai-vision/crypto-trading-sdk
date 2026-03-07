package com.meaivision.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.meaivision.trading.base.model.AccountInfoFutures;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitAccountInfoFutures extends AccountInfoFutures {}
