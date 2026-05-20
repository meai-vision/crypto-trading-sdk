package com.meaivision.trading.binance.stream;

import com.binance.connector.client.enums.DefaultUrls;
import lombok.Builder;
import lombok.Value;

/**
 * Binance REST + WebSocket endpoint set. Use {@link #mainnet()} or {@link #testnet()} or supply
 * your own with {@link #builder()}.
 */
@Value
@Builder
public class BinanceStreamUrls {
  String restBaseUrl;
  String wsStreamBaseUrl;

  public static BinanceStreamUrls mainnet() {
    return BinanceStreamUrls.builder()
        .restBaseUrl(DefaultUrls.PROD_URL)
        .wsStreamBaseUrl(DefaultUrls.WS_URL)
        .build();
  }

  public static BinanceStreamUrls testnet() {
    return BinanceStreamUrls.builder()
        .restBaseUrl(DefaultUrls.TESTNET_URL)
        .wsStreamBaseUrl(DefaultUrls.TESTNET_WS_URL)
        .build();
  }
}
