package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.Utils;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.RecomendacaoAporte;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CalcularRecomendacaoAporteUseCase {

    /**
     * Distribui o aporte de cada classe entre os ativos elegíveis (nota > 0),
     * ponderando pelo peso relativo da nota dentro da classe.
     *
     * @param aportesPorClasse valor do aporte já normalizado por TipoAtivo
     * @param ativos           conjunto completo de ativos da carteira
     */
    public List<RecomendacaoAporte> calcular(Map<TipoAtivo, Double> aportesPorClasse, Set<Ativo> ativos) {
        // Pré-calcula a soma das notas por classe (apenas ativos elegíveis)
        Map<TipoAtivo, Double> somaNotasPorClasse = ativos.stream()
                .filter(a -> a.getNota() > 0)
                .collect(Collectors.groupingBy(
                        Ativo::getTipoAtivo,
                        Collectors.summingDouble(Ativo::getNota)
                ));

        return ativos.stream().map(ativo -> {
                    var aporteClasse = aportesPorClasse.getOrDefault(ativo.getTipoAtivo(), 0.0);
                    if (aporteClasse <= 0 || ativo.getNota() <= 0) {
                        return new RecomendacaoAporte(BigDecimal.ZERO, ativo);
                    }
                    var somaNotas = somaNotasPorClasse.getOrDefault(ativo.getTipoAtivo(), 0.0);
                    var peso = somaNotas > 0 ? (double) ativo.getNota() / somaNotas : 0.0;
                    var aporteAtivo = Utils.arredondamentoPadrao(aporteClasse * peso);
                    return new RecomendacaoAporte(BigDecimal.valueOf(aporteAtivo), ativo);
                }).sorted(Comparator.comparing(RecomendacaoAporte::recomendacao))
                .collect(Collectors.toList());
    }
}
