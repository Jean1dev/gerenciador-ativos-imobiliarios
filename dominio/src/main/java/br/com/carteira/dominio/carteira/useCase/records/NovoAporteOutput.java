package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.useCase.records.RecomendacaoAporte;

import java.util.List;
import java.util.Set;

public record NovoAporteOutput(
        List<RecomendacaoAporte> recomendacaoAporteList,
        Set<MetaComValorRecomendado> metaComValorRecomendados
) {
}
