package br.com.carteira.dominio.cotacao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Cotacao")
class CotacaoTest {

    @Test
    @DisplayName("deve criar o objeto corretamente")
    public void deveCriar() {
        var petr4 = new Cotacao("PETR4", 25.7989);
        assertNotNull(petr4);
        assertEquals("PETR4", petr4.getPapel());
        assertEquals(25.7989, petr4.getCotacaoNoMomento());
    }

}