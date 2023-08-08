package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.useCase.CalcularRecomendacaoAporteUseCase;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.useCase.records.MetaComValorRecomendado;
import br.com.carteira.dominio.carteira.useCase.records.NovoAporteOutput;
import br.com.carteira.dominio.exception.DominioException;

import java.util.Objects;
import java.util.stream.Collectors;

public class NovoAporteUseCase {

    public NovoAporteOutput execute(Double valorAporte, Carteira carteira) {
        if (valorAporte.isNaN() ||
                valorAporte.isInfinite() ||
                valorAporte.doubleValue() < 0) {
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

        final var metaComValorRecomendados = pesoDaMeta.stream().map(meta -> {
            final var ativoComPercentualETotal = metasPercentuais.get(meta.getTipoAtivo().descricao());

            final var valorRecomendado =
                    (valorFinalComAporte * (meta.getPercentual() / 100)) - ativoComPercentualETotal.valor();

            return new MetaComValorRecomendado(meta.getTipoAtivo(), valorRecomendado);
        }).collect(Collectors.toSet());

        final var recomendacaoAporteList = new CalcularRecomendacaoAporteUseCase()
                .calcular(valorAporte, carteira.getAtivos());

        return new NovoAporteOutput(recomendacaoAporteList, metaComValorRecomendados);
    }

}