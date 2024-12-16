package br.com.carteira.infra.integracoes.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CoinMarketCapResponse(
        List<DataResponse> data
) {
}

record DataResponse(
        String name,
        String symbol,
        Quote quote
) {
}

record Quote(
        @JsonProperty("USD")
        USDPrice usd
) {
}

record USDPrice(
        Double price
) {
}
