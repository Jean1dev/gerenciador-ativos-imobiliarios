package br.com.carteira.infra.ativo.records;

import java.time.LocalDate;

public record VariacaoAtivo(
        String ticker,
        int diasPassados,
        Double percentualVariacao,
        LocalDate inicio,
        LocalDate fim
) {
}
