package com.meaivision.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.meaivision.trading.base.model.AccountInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BybitAccountInfo extends AccountInfo {}
