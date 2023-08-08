package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;

public record AtivoComPercentualETotal(
        TipoAtivo tipoAtivo,
        double valor,
        double percentual
) {
}
