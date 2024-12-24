package br.com.carteira.dominio.marketplace.compra.ativos.useCase;

import br.com.carteira.dominio.DomainTests;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.exception.DominioException;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import br.com.carteira.dominio.marketplace.compra.ativos.records.PedidoCompraDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrdemCompraUseCasesTest extends DomainTests {

    @Mock
    private OrdemCompraGateway gateway;
    @InjectMocks
    private OrdemCompraUseCases useCases;

    @Test
    void executarPedidoCompraAtivo() {
        var input = new PedidoCompraDTO("ativo", "usuario", 10);

        Ativo ativo = new AcaoNacional("ativo", 10, 10, 10, 10, 10);
        when(gateway.buscarAtivo("ativo")).thenReturn(Optional.of(ativo));
        when(gateway.verificarSeUsuarioPossuiSaldo("usuario", 10)).thenReturn(true);

        useCases.executarPedidoCompraAtivo(input);

        verify(gateway, times(1)).registrarOrdem(any(OrdemCompra.class));
    }

    @Test
    void deveFalharPorqueUsuarioNaoTemSaldo() {
        var input = new PedidoCompraDTO("ativo", "usuario", 10);

        Ativo ativo = new AcaoNacional("ativo", 10, 10, 10, 10, 10);
        when(gateway.buscarAtivo("ativo")).thenReturn(Optional.of(ativo));
        when(gateway.verificarSeUsuarioPossuiSaldo("usuario", 10)).thenReturn(false);

        assertThrows(DominioException.class, () -> useCases.executarPedidoCompraAtivo(input));
    }
}