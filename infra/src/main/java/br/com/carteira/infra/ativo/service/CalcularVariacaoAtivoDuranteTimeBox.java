package br.com.carteira.infra.ativo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class CalcularVariacaoAtivoDuranteTimeBox {

    public static VariacapDePreco calc(
            Double oldValue,
            LocalDateTime oldDate,
            Double newValue,
            LocalDateTime newDate
    ) {
        int diferencaDeDias = contarDiferencaDeDias(oldDate, newDate);

        double variacaoAbsoluta = newValue - oldValue;

        double percentualVariacao = (variacaoAbsoluta / oldValue) * 100;
        if (Double.isInfinite(percentualVariacao) || Double.isNaN(percentualVariacao))
            percentualVariacao = 0.0;

        return new VariacapDePreco(diferencaDeDias, percentualVariacao);
    }

    private static int contarDiferencaDeDias(LocalDateTime oldDate, LocalDateTime newDate) {
        return (int) ChronoUnit.DAYS.between(oldDate, newDate);
    }

    public record VariacapDePreco(
            int diferencaDeDias,
            Double percentualVariacao
    ) {
    }
}
