package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.useCase.CalcularValorRecomendadoUseCase;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.Meta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Carlcular Percentual da Carteira")
class CalcularPercentualCarteiraEmMetasUseCaseTest {

    @Test
    @DisplayName("Deve calcular corretamente")
    public void deveCalcular() {
        final var useCase = new CalcularValorRecomendadoUseCase();
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());
    }

}