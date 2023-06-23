package br.com.carteira.dominio.cotacao;

import java.util.Objects;

public class Cotacao {
    private String papel;
    private double cotacaoNoMomento;

    public Cotacao(String papel, double cotacaoNoMomento) {
        this.papel = Objects.requireNonNull(papel, "papel nao pode ser nullo");
        this.cotacaoNoMomento = Objects.requireNonNull(cotacaoNoMomento, "cotacaoNoMomento não pode ser nullo");
    }

    public String getPapel() {
        return papel;
    }

    public double getCotacaoNoMomento() {
        return cotacaoNoMomento;
    }
}
