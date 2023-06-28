package br.com.carteira.dominio.carteira;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Carteira test")
class CarteiraTest {

    private Carteira umaCarteiraComAtivos() {
        AcaoNacional petr4 = AcaoNacional.fromSimplificado(
                new AtivoSimplificado(
                        TipoAtivo.ACAO_NACIONAL,
                        "PETR4",
                        1,
                        1
                )
        );

        AcaoNacional vale3 = AcaoNacional.fromSimplificado(
                new AtivoSimplificado(
                        TipoAtivo.ACAO_NACIONAL,
                        "VALE3",
                        1,
                        1
                )
        );

        AcaoInternacional ibm = AcaoInternacional.fromSimplificado(
                new AtivoSimplificado(
                        TipoAtivo.ACAO_INTERNACIONAL,
                        "IBM",
                        1,
                        1
                )
        );

        Carteira carteira = new Carteira();
        carteira.setAtivos(Set.of(petr4, vale3, ibm));
        return carteira;
    }

    @DisplayName("deve retornar somente acoes internacionais")
    @Test
    public void acoesInternacionais() {
        Carteira carteira = umaCarteiraComAtivos();
        final var acoesInternacionais = carteira.getAcoesInternacionais();
        assertEquals(acoesInternacionais.size(), 1);
        assertTrue(acoesInternacionais
                .stream()
                .map(AcaoInternacional::getTicker)
                .toList()
                .get(0)
                .contains("IBM"));
    }


    @DisplayName("deve retornar somente acoes nacionais")
    @Test
    public void acoesNacionais() {
        final var carteira = umaCarteiraComAtivos();
        Set<AcaoNacional> acoesNacionais = carteira.getAcoesNacionais();
        assertEquals(2, acoesNacionais.size());
        Set<String> tickers = acoesNacionais
                .stream()
                .map(AcaoNacional::getTicker)
                .collect(Collectors.toUnmodifiableSet());
        assertTrue(tickers.contains("VALE3"));
        assertTrue(tickers.contains("PETR4"));
        assertFalse(tickers.contains("IBM"));
    }

}