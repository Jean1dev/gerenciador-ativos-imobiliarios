package br.com.carteira.infra.utils;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.records.VariacaoAtivo;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class Utils {
    private static final Pattern ACAO_PATTERN = Pattern.compile("^[A-Z]{4}\\d{1,2}$");

    public static String getTickerParaPesquisa(AtivoComCotacao ativoComCotacao) {
        if (TipoAtivo.ACAO_NACIONAL.equals(ativoComCotacao.getTipoAtivo())) {
            return ativoComCotacao.getTicker() + ".SAO";
        }

        return ativoComCotacao.getTicker();
    }

    public static VariacaoAtivo emptyVariationWithTicker(String ticker) {
        return new VariacaoAtivo(ticker, 0, 0.0, LocalDate.now(), LocalDate.now());
    }

    public static boolean ehAcaoNacional(String codigo) {
        if (codigo == null) {
            return false;
        }
        // Verifica se o formato é válido
        if (!ACAO_PATTERN.matcher(codigo).matches()) {
            return false;
        }
        // Extrai o número final
        int numero = Integer.parseInt(codigo.substring(4));
        // Verifica se é um código válido
        return numero == 3 || numero == 4 || (numero >= 5 && numero <= 8) || numero == 11;
    }
}
