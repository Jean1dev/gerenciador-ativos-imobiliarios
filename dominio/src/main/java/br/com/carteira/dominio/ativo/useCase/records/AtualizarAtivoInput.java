package br.com.carteira.dominio.ativo.useCase.records;

public record AtualizarAtivoInput(
        Double quantidade,
        String nome,
        Integer nota,
        String identificacao
) {
}
