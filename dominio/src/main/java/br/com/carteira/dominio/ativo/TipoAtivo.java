package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.EnumDominio;

public enum TipoAtivo implements EnumDominio {
    ACAO_NACIONAL("Ações Nacionais"),
    ACAO_INTERNACIONAL("Ações Internacionais"),
    REITs("Real Estate"),
    FII("Fundos Imobiliarios"),
    CRYPTO("Cryptomoedas"),
    RENDA_FIXA("Renda Fixa");

    private final String descricao;

    TipoAtivo(String tipo) {
        descricao = tipo;
    }

    @Override
    public String descricao() {
        return descricao;
    }
}
