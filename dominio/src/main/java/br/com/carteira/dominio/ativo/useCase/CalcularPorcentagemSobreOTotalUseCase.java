package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.Ativo;

import java.util.Set;

import static br.com.carteira.dominio.Utils.arredondamentoPadrao;

public final class CalcularPorcentagemSobreOTotalUseCase {

    public double calcular(Ativo novoAtivo, Set<Ativo> ativoList) {
        var totalAtivos = ativoList.stream()
                .mapToDouble(value -> value.getValorAtual() * value.getQuantidade())
                .sum();

        if (totalAtivos == 0) {
            return 100;
        }

        var valorNovoAtivo = novoAtivo.getValorAtual();
        var valorTotalNovoAtivo = valorNovoAtivo * novoAtivo.getQuantidade();
        var porcentagemDoNovoAtivoSobOTotal = (valorTotalNovoAtivo / totalAtivos) * 100;
        return arredondamentoPadrao(porcentagemDoNovoAtivoSobOTotal);
    }
}
