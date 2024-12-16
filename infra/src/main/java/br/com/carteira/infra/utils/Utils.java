package br.com.carteira.infra.utils;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;

public final class Utils {

    public static String getTickerParaPesquisa(AtivoComCotacao ativoComCotacao) {
        if (TipoAtivo.ACAO_NACIONAL.equals(ativoComCotacao.getTipoAtivo())) {
            return ativoComCotacao.getTicker() + ".SAO";
        }

        return ativoComCotacao.getTicker();
    }
}
