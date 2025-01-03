package br.com.carteira.dominio;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public final class Utils {

    public static Object nullOrValue(Object comparator, Object orElse) {
        if (Objects.isNull(comparator))
            return orElse;

        return comparator;
    }

    public static double arredondamentoPadrao(double valor) {
        var decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return Double.parseDouble(decimalFormat.format(valor).replace(",", "."));
    }

    public static double seNegativoEntaoRetornaZero(double valor) {
        return valor < 0 ? 0 : valor;
    }

    public static String generateUUID_String() {
        return java.util.UUID.randomUUID().toString();
    }
}
