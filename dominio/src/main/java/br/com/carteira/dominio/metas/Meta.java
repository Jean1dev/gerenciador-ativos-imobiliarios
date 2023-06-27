package br.com.carteira.dominio.metas;

import br.com.carteira.dominio.ativo.TipoAtivo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public class Meta {
    private Set<AtivoComPercentual> ativoComPercentuals;

    public Meta() {
        resetarMetas();
    }
    public static Meta metasDoJeanluca() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(20, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(30, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(0, TipoAtivo.REITs),
                new AtivoComPercentual(18, TipoAtivo.FII),
                new AtivoComPercentual(6, TipoAtivo.CRYPTO),
                new AtivoComPercentual(26, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    public static Meta conservador() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(11.5, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(11.5, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(11.5, TipoAtivo.REITs),
                new AtivoComPercentual(5.5, TipoAtivo.FII),
                new AtivoComPercentual(0, TipoAtivo.CRYPTO),
                new AtivoComPercentual(60, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    public static Meta moderado() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(15, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(10, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(10, TipoAtivo.REITs),
                new AtivoComPercentual(15, TipoAtivo.FII),
                new AtivoComPercentual(0, TipoAtivo.CRYPTO),
                new AtivoComPercentual(50, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    public static Meta dinamico() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(20, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(10, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(7, TipoAtivo.REITs),
                new AtivoComPercentual(20, TipoAtivo.FII),
                new AtivoComPercentual(3, TipoAtivo.CRYPTO),
                new AtivoComPercentual(40, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    public static Meta arrojado() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(15, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(10, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(15, TipoAtivo.REITs),
                new AtivoComPercentual(25, TipoAtivo.FII),
                new AtivoComPercentual(5, TipoAtivo.CRYPTO),
                new AtivoComPercentual(30, TipoAtivo.RENDA_FIXA)
        );
        return meta;
    }

    public static Meta sofisticado() {
        var meta = new Meta();
        meta.ativoComPercentuals = Set.of(
                new AtivoComPercentual(25, TipoAtivo.ACAO_NACIONAL),
                new AtivoComPercentual(25, TipoAtivo.ACAO_INTERNACIONAL),
                new AtivoComPercentual(0, TipoAtivo.REITs),
                new AtivoComPercentual(30, TipoAtivo.FII),
                new AtivoComPercentual(10, TipoAtivo.CRYPTO),
                new AtivoComPercentual(10, TipoAtivo.RENDA_FIXA)
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
                .collect(Collectors.toSet());
    }

    public Set<AtivoComPercentual> getAtivoComPercentuals() {
        return ativoComPercentuals;
    }
}
