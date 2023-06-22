package br.com.carteira.dominio.metas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Metas test")
class MetasTest {

    @DisplayName("Deve construir e setar as metas corretamentes")
    @Test
    public void deveFazerCerto() {
        Meta metas = new Meta();
        Assertions.assertNotNull(metas);
        var soma = metas.getAtivoComPercentuals()
                .stream()
                .mapToDouble(AtivoComPercentual::getPercentual)
                .sum();
        Assertions.assertTrue(soma < 101);
        Assertions.assertTrue(soma >= 100);
    }

}