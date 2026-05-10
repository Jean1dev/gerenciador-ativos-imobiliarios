package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.CalcularRecomendacaoAporteUseCase;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.useCase.records.MetaComValorRecomendado;
import br.com.carteira.dominio.carteira.useCase.records.NovoAporteOutput;
import br.com.carteira.dominio.exception.DominioException;
import br.com.carteira.dominio.metas.AtivoComPercentual;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public class NovoAporteUseCase {

    public NovoAporteOutput execute(Double valorAporte, Carteira carteira) {
        if (valorAporte.isNaN() || valorAporte.isInfinite() || valorAporte < 0) {
            throw new DominioException("Valor do aporte nao permitido");
        }

        if (Objects.isNull(carteira.getMeta())) {
            throw new DominioException("Carteira nao tem uma meta definida");
        }

        final var totalCarteira = carteira.getAtivos().stream()
                .mapToDouble(ativo -> ativo.getQuantidade() * ativo.getValorAtual())
                .sum();

        if (totalCarteira == 0)
            throw new DominioException("Carteira com valor total zero");

        final var valorFinalComAporte = totalCarteira + valorAporte;
        final var metasPercentuais = new CalcularPercentualCarteiraEmMetasUseCase().executar(carteira);
        final var pesoDaMeta = carteira.getMeta().getAtivoComPercentuals();

        // 1. Calcula o gap (deficit) de cada classe em relação ao alvo
        Map<TipoAtivo, Double> gapPorClasse = new HashMap<>();
        for (AtivoComPercentual meta : pesoDaMeta) {
            var ativoComPercentualETotal = metasPercentuais.get(meta.getTipoAtivo().descricao());
            if (ativoComPercentualETotal == null) continue;
            var valorIdeal = valorFinalComAporte * (meta.getPercentual() / 100);
            var gap = valorIdeal - ativoComPercentualETotal.valor();
            if (gap > 0) {
                gapPorClasse.put(meta.getTipoAtivo(), gap);
            }
        }

        // 2. Mantém apenas classes com gap positivo que possuem ao menos um ativo elegível (nota > 0)
        Map<TipoAtivo, Double> gapsElegiveis = gapPorClasse.entrySet().stream()
                .filter(entry -> carteira.getAtivos().stream()
                        .anyMatch(a -> a.getTipoAtivo().equals(entry.getKey()) && a.getNota() > 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 3. Normaliza os gaps para que a soma dos aportes por classe seja exatamente valorAporte
        var somaGapsElegiveis = gapsElegiveis.values().stream().mapToDouble(d -> d).sum();

        Map<TipoAtivo, Double> aportesPorClasse = new HashMap<>();
        if (somaGapsElegiveis > 0) {
            gapsElegiveis.forEach((tipoAtivo, gap) ->
                    aportesPorClasse.put(tipoAtivo, (gap / somaGapsElegiveis) * valorAporte));
        }

        // 4. Monta o resultado por classe com o valor normalizado do aporte
        final var metaComValorRecomendados = pesoDaMeta.stream()
                .map(meta -> new MetaComValorRecomendado(
                        meta.getTipoAtivo(),
                        arredondamentoPadrao(aportesPorClasse.getOrDefault(meta.getTipoAtivo(), 0.0))
                ))
                .collect(Collectors.toSet());

        // 5. Distribui o aporte de cada classe entre os ativos da classe, ponderado pela nota
        final var recomendacaoAporteList = new CalcularRecomendacaoAporteUseCase()
                .calcular(aportesPorClasse, carteira.getAtivos());

        return new NovoAporteOutput(recomendacaoAporteList, metaComValorRecomendados);
    }
}
