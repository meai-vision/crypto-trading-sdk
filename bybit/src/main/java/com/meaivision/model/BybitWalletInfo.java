package com.meaivision.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BybitWalletInfo {

    @JsonProperty("result")
    private List<WalletCoin> coins;

    public List<WalletCoin> getCoins() {
        return coins;
    }

    public void setCoins(List<WalletCoin> coins) {
        this.coins = coins;
    }

    @Data
    public static class WalletCoin {

        @JsonProperty("coin")
        private String coin;

        @JsonProperty("equity")
        private BigDecimal equity;

        @JsonProperty("availableBalance")
        private BigDecimal availableBalance;

        @JsonProperty("walletBalance")
        private BigDecimal walletBalance;

        @JsonProperty("unrealisedPnl")
        private BigDecimal unrealisedPnl;

    }
}