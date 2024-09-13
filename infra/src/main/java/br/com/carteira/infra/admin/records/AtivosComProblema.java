package br.com.carteira.infra.admin.records;

import java.time.LocalDateTime;

public record AtivosComProblema(
        String ticker,
        LocalDateTime when,
        String message
) {
}
