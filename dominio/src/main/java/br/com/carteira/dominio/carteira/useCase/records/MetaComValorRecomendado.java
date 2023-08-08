package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;

public record MetaComValorRecomendado(
        TipoAtivo tipoAtivo,
        Double valorRecomendado
) {
}
