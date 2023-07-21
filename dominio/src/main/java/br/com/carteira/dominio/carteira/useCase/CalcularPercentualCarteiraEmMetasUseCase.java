package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.AtivoComPercentual;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public class CalcularPercentualCarteiraEmMetasUseCase {

    public Map<String, Double> executar(Carteira carteira) {
        if (Objects.isNull(carteira) || Objects.isNull(carteira.getMeta()))
            return Map.of();

        final var meta = carteira.getMeta();
        final var totalCarteira = carteira.getAtivos().stream()
                .mapToDouble(ativo -> ativo.getQuantidade() * ativo.getValorAtual())
                .sum();

        if (totalCarteira == 0)
            return Map.of();

        final var tipoAtivoComTotals = meta.getAtivoComPercentuals()
                .stream()
                .map(AtivoComPercentual::getTipoAtivo)
                .collect(Collectors.toSet())
                .stream()
                .map(tipoAtivo -> {
                    final var totalPorTipoAtivo = carteira.getAtivos()
                            .stream().filter(ativo -> tipoAtivo.equals(ativo.getTipoAtivo()))
                            .mapToDouble(ativo -> ativo.getQuantidade() * ativo.getValorAtual())
                            .sum();

                    return new TipoAtivoComTotal(tipoAtivo, totalPorTipoAtivo);
                })
                .collect(Collectors.toSet());

        HashMap<String, Double> map = new HashMap<>();
        tipoAtivoComTotals.forEach(tipoAtivoComTotal -> {
            if (tipoAtivoComTotal.totalPorTipoAtivo == 0) {
                map.put(tipoAtivoComTotal.tipoAtivo.descricao(), 0.0);
                return;
            }
            var percentualDeCada = (tipoAtivoComTotal.totalPorTipoAtivo / totalCarteira) * 100;
            var percentaulArredondado = arredondamentoPadrao(percentualDeCada);
            map.put(tipoAtivoComTotal.tipoAtivo.descricao(), percentaulArredondado);
        });

        return map;
    }

    private class TipoAtivoComTotal {
        TipoAtivo tipoAtivo;
        double totalPorTipoAtivo;

        public TipoAtivoComTotal(TipoAtivo tipoAtivo, double totalPorTipoAtivo) {
            this.tipoAtivo = tipoAtivo;
            this.totalPorTipoAtivo = totalPorTipoAtivo;
        }
    }
}
