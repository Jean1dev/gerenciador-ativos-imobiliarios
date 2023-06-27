package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;

public record AtivoSimplificado(
        TipoAtivo tipoAtivo,
        String papel,
        double quantidade,
        int nota
) {
}
