package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.Ativo;

import java.util.Set;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public final class CalcularValorRecomendadoUseCase {

//    public double _calcular(Ativo novoAtivo, Meta meta, Set<Ativo> ativos) throws DominioException {
//        var percentagemSobreOTotal = new CalcularPorcentagemSobreOTotalUseCase().calcular(novoAtivo, ativos);
//
//        // 20%
//        var metaPercentualParaEsseAtivo = meta.getAtivoComPercentuals()
//                .stream()
//                .filter(it -> novoAtivo.getTipoAtivo().equals(it.getTipoAtivo()))
//                .findFirst()
//                .orElseThrow(() -> new DominioException("Não existe uma ativo configurado nessa meta"))
//                .getPercentual();
//
//        // 14976.38
//        var totalParaEsseAtivo = ativos
//                .stream()
//                .filter(ativo -> novoAtivo.getTipoAtivo().equals(ativo.getTipoAtivo()))
//                .mapToDouble(value -> value.getValorAtual() * value.getQuantidade())
//                .sum();
//
//        // 82.646,94
//        var totalAtivos = ativos
//                .stream()
//                .mapToDouble(value -> value.getValorAtual() * value.getQuantidade())
//                .sum();
//
//        // 18.12%
//        var percentualParaEsseAtivo = (totalParaEsseAtivo / totalAtivos) * 100;
//        return  0;
//    }

    public double calcular(Ativo novoAtivo, Set<Ativo> ativos) {
        Set<Ativo> collect = ativos.stream()
                .filter(ativo -> novoAtivo.getTipoAtivo().equals(ativo.getTipoAtivo()))
                .collect(Collectors.toSet());

        collect.add(novoAtivo);

        var totalNotas = collect.stream().mapToDouble(Ativo::getNota).sum();

        if (novoAtivo.getNota() == 0)
            return 0;

        var recomendado = (novoAtivo.getNota() / totalNotas) * 100;
        return arredondamentoPadrao(recomendado);
    }
}
