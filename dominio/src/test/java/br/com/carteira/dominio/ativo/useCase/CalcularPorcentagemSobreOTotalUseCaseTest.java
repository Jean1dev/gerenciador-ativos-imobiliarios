package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.exception.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CalcularPorcentagemSobreOTotal")
class CalcularPorcentagemSobreOTotalUseCaseTest {

    @Test
    @DisplayName("deve calcular a porcentagem corretamente sobre um novo ativo")
    public void deveCalcular() throws DominioException {
        var petr4 = new AcaoNacional(
                "PETR4",
                0,
                5.95,
                3,
                0,
                1
        );

        var useCase = new CalcularPorcentagemSobreOTotalUseCase();
        var resultado = useCase.calcular(petr4, new HashSet<>());
        assertEquals(100, resultado);

        var klbn11 = new AcaoNacional(
                "KLBN11",
                0,
                5.95,
                3,
                0,
                10
        );

        var vale3 = new AcaoNacional(
                "VALE3",
                0,
                40.85,
                3,
                0,
                1
        );

        resultado = useCase.calcular(petr4, Set.of(vale3, klbn11));
        assertEquals(5.9, resultado);
    }

}