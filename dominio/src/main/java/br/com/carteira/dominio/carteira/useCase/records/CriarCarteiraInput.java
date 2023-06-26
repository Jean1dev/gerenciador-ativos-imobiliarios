package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.dominio.metas.MetaDefinida;

import java.util.Collection;

public record CriarCarteiraInput(
        String nome,
        Meta meta,
        Collection<AtivoSimplificado> ativos,
        MetaDefinida metaDefinida
) {
    public static CriarCarteiraInput byName(String nome) {
        return new CriarCarteiraInput(nome, null, null, null);
    }

}
