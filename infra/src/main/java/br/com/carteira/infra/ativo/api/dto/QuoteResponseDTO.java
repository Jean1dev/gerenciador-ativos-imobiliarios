package br.com.carteira.infra.ativo.api.dto;

import java.io.Serializable;

public class QuoteResponseDTO implements Serializable {
    private String ticker;
    private String type;
    private double variation;

    public QuoteResponseDTO(String ticker, String type, double variation) {
        this.ticker = ticker;
        this.type = type;
        this.variation = variation;
    }

    public String getTicker() {
        return ticker;
    }

    public String getType() {
        return type;
    }

    public double getVariation() {
        return variation;
    }
}
