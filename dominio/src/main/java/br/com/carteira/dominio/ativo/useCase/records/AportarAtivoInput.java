package br.com.carteira.dominio.ativo.useCase.records;

public record AportarAtivoInput(
        String id,
        double quantidade
) {
}
