package com.meaivision.trading.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingClientSettings {
  private String apiKey;
  private String secretKey;
  private String passphrase;
}
