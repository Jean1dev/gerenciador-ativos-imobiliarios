package br.com.carteira.dominio.ativo.useCase.records;

import br.com.carteira.dominio.ativo.Ativo;

import java.math.BigDecimal;

public record RecomendacaoAporte(
        BigDecimal recomendacao,
        Ativo ativo
) {
}
