package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.TipoAtivo;
import br.com.carteira.dominio.exception.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Ação Nacional test")
class AcaoNacionalTest {

    @Test
    @DisplayName("deve criar o objeto corretamente")
    public void deveCriar() throws DominioException {
        var acaoNacional = new AcaoNacional("PETR4", 2.5, 50, 5, 10.0, 1);
        assertNotNull(acaoNacional);
        assertEquals("PETR4", acaoNacional.getTicker());
        assertEquals(TipoAtivo.ACAO_NACIONAL, acaoNacional.getTipoAtivo());
        assertEquals("B3", acaoNacional.getLocalAlocado());
    }

    @Test
    @DisplayName("nao deve criar o objeto pq o ticker é nullo")
    public void deveLancar() {
        assertThrows(NullPointerException.class, () ->
                new AcaoNacional(null, 2.5, 50, 5, 10.0, 1));
    }

    @Test
    @DisplayName("nao deve criar o objeto pq a quantidade é negativa")
    public void naoPodeCriarComQuantidadeNegativa() {
        assertThrows(DominioException.class, () ->
                new AcaoNacional("APPL3", 2.5, 50, 5, 10.0, -1));
    }

}