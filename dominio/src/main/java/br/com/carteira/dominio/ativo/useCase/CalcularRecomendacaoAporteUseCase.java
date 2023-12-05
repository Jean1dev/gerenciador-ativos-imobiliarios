package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.Utils;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.useCase.records.RecomendacaoAporte;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CalcularRecomendacaoAporteUseCase {

    public List<RecomendacaoAporte> calcular(double valorAporte, Set<Ativo> ativos) {
        var valorTotal = ativos.stream()
                .mapToDouble(value -> value.getValorAtual() * value.getQuantidade())
                .sum();

        /*
        * valor do aporte, percentual de quanto eu deveria ter no ativo
        *  x = vlAporte * (percentualAtivo / 100) -
        * */

        var novoValorTotal = valorTotal + valorAporte;
        return ativos.stream().map(ativo -> {
                    var percentualRecomendado = ativo.getPercentualRecomendado();
                    var valorAtual = ativo.getValorAtual() * ativo.getQuantidade();
                    var valorSugestao = (novoValorTotal * (percentualRecomendado / 100)) - valorAtual;
                    var valorFormatadoFinal = Utils.seNegativoEntaoRetornaZero(Utils.arredondamentoPadrao(valorSugestao));
                    return new RecomendacaoAporte(BigDecimal.valueOf(valorFormatadoFinal), ativo);
                }).sorted(Comparator.comparing(RecomendacaoAporte::recomendacao))
                .collect(Collectors.toList());
    }
}
