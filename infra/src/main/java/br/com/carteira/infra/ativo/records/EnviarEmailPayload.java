package br.com.carteira.infra.ativo.records;

public record EnviarEmailPayload(
        String to,
        String subject,
        String message
) {
}
