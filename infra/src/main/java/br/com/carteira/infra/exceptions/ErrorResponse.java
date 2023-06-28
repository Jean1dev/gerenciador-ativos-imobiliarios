package br.com.carteira.infra.exceptions;

import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details,
        int statusCode
) {
}
