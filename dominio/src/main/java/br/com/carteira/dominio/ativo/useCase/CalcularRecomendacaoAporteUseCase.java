package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.useCase.records.RecomendacaoAporte;
import br.com.carteira.dominio.metas.Meta;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CalcularRecomendacaoAporteUseCase {

    public List<RecomendacaoAporte> calcular(double valorAporte, Meta meta, Set<Ativo> ativos) {
        var valorTotal = ativos.stream()
                .mapToDouble(value -> value.getValorAtual() * value.getQuantidade())
                .sum();

        var novoValorTotal = valorTotal + valorAporte;
        return ativos.stream().map(ativo -> {
                    var percentualRecomendado = ativo.getPercentualRecomendado();
                    var valorAtual = ativo.getValorAtual() * ativo.getQuantidade();
                    var valorSugestao = (novoValorTotal * percentualRecomendado) - valorAtual;
                    return new RecomendacaoAporte(BigDecimal.valueOf(valorSugestao), ativo);
                }).sorted(Comparator.comparing(RecomendacaoAporte::recomendacao))
                .collect(Collectors.toList());
    }
}
