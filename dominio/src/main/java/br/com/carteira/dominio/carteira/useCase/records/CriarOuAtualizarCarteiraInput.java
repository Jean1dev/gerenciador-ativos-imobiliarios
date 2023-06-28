package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.dominio.metas.MetaDefinida;

import java.util.Collection;

public record CriarOuAtualizarCarteiraInput(
        String nome,
        Meta meta,
        Collection<AtivoSimplificado> ativos,
        MetaDefinida metaDefinida,
        String identificacaoCarteiraJaCriada
) {
    public static CriarOuAtualizarCarteiraInput byName(String nome) {
        return new CriarOuAtualizarCarteiraInput(nome, null, null, null, null);
    }

}
