package br.com.carteira.dominio.criterios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Criterios test")
class CriteriosTest {

    @Test
    @DisplayName("deve criar com sucesso")
    public void deveCriar() {
        var criterios = new Criterio("ROE", "pergunta");
        assertNotNull(criterios);
        assertFalse(criterios.getSimOuNao());
        assertEquals("ROE", criterios.getCriterio());
        assertEquals("pergunta", criterios.getPergunta());

        criterios.setSimOuNao(true);

        assertTrue(criterios.getSimOuNao());
    }

}