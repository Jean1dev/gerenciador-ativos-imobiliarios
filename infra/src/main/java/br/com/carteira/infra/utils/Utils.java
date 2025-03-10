package br.com.carteira.infra.utils;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.records.VariacaoAtivo;

import java.time.LocalDate;

public final class Utils {

    public static String getTickerParaPesquisa(AtivoComCotacao ativoComCotacao) {
        if (TipoAtivo.ACAO_NACIONAL.equals(ativoComCotacao.getTipoAtivo())) {
            return ativoComCotacao.getTicker() + ".SAO";
        }

        return ativoComCotacao.getTicker();
    }

    public static VariacaoAtivo emptyVariationWithTicker(String ticker) {
        return new VariacaoAtivo(ticker, 0, 0.0, LocalDate.now(), LocalDate.now());
    }
}
