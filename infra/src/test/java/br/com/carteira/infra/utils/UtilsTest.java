package br.com.carteira.infra.utils;

import org.junit.jupiter.api.Test;

import static br.com.carteira.infra.utils.Utils.ehAcaoNacional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    @Test
    void deveValidarSeEhOuNaoAcaoNacional() {
        assertTrue(ehAcaoNacional("PETR4"));
        assertTrue(ehAcaoNacional("VALE3"));
        assertTrue(ehAcaoNacional("ITUB11"));
        assertFalse(ehAcaoNacional("XXXX9"));
        assertFalse(ehAcaoNacional("TESTE"));
    }
}