package br.com.carteira.dominio.metas;

import br.com.carteira.dominio.EnumDominio;

public enum MetaDefinida implements EnumDominio {
    META_DO_JEAN("meta do jean");

    private final String descricao;

    MetaDefinida(String tipo) {
        descricao = tipo;
    }

    @Override
    public String descricao() {
        return descricao;
    }
}
