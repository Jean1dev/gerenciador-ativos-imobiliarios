package br.com.carteira.infra.admin.api.dto;

public record AtualizarAtivoComCotacaoInput(
        String nome,
        String imageUrl,
        Double valor
) {
}
