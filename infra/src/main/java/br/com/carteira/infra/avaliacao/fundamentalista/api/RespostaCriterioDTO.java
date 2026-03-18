package br.com.carteira.infra.avaliacao.fundamentalista.api;

public record RespostaCriterioDTO(
        String pergunta,
        boolean resposta,
        String justificativa
) {
}
