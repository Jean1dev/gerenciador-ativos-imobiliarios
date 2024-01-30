package br.com.carteira.infra.integracoes.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class AlphavanteGetQuotaResponse implements Serializable {

    @JsonProperty("Information")
    private String Information;
    @JsonProperty("Global Quote")
    private GlobalQuote globalQuote;

    public AlphavanteGetQuotaResponse() {
    }

    public GlobalQuote getGlobalQuote() {
        return globalQuote;
    }

    public String getInformation() {
        return Information;
    }

    public void setGlobalQuote(GlobalQuote globalQuote) {
        this.globalQuote = globalQuote;
    }

    public class GlobalQuote {

        @JsonProperty("01. symbol")
        private String symbol;
        @JsonProperty("05. price")
        private Double price;
        @JsonProperty("07. latest trading day")
        private String lastTradingDay;

        public GlobalQuote() {
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getLastTradingDay() {
            return lastTradingDay;
        }

        public void setLastTradingDay(String lastTradingDay) {
            this.lastTradingDay = lastTradingDay;
        }
    }
}
