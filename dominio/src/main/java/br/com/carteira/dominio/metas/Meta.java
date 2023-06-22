package br.com.carteira.dominio.metas;

import br.com.carteira.dominio.TipoAtivo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public class Meta {
    private List<AtivoComPercentual> ativoComPercentuals;

    public Meta() {
        resetarMetas();
    }

    public static Meta metasDoJeanluca() {
        var meta = new Meta();
        meta.ativoComPercentuals = List.of(
                new AtivoComPercentual(20, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(30, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(0, TipoAtivo.REITs),
                new AtivoComPercentual(18, TipoAtivo.FII),
                new AtivoComPercentual(6, TipoAtivo.CRYPTO),
                new AtivoComPercentual(26, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    private void resetarMetas() {
        var percentualMaximo = 100;
        var totalTipoAtivos = TipoAtivo.values().length;
        var percentualDeCada = (double) percentualMaximo / totalTipoAtivos;
        var percentaulArredondado = arredondamentoPadrao(percentualDeCada);

        ativoComPercentuals = Arrays.stream(TipoAtivo.values())
                .map(tipoAtivo -> new AtivoComPercentual(percentaulArredondado, tipoAtivo))
                .collect(Collectors.toList());
    }

    public List<AtivoComPercentual> getAtivoComPercentuals() {
        return ativoComPercentuals;
    }
}
