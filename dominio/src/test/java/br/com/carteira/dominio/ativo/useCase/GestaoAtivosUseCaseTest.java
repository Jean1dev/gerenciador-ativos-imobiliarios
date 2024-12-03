package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.DomainTests;
import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class GestaoAtivosUseCaseTest extends DomainTests {
    @Mock
    private CarteiraGateway carteiraGateway;
    @Mock
    private AtivosComTickerGateway ativosComTickerGateway;
    @InjectMocks
    private GestaoAtivosUseCase useCase;

    @DisplayName("deve adicionar ativo com crypto")
    @Test
    void adicionarAtivoComCrypto() {
        var input = new AdicionarAtivoInput(TipoAtivo.CRYPTO, 10, 0.0, "", "", 1.0, "BTC", "", null);
    }
}