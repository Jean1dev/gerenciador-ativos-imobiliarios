package br.com.carteira.infra.ativo.component;

public record EnviarEmailPayload(
        String to,
        String subject,
        String message
) {
}
