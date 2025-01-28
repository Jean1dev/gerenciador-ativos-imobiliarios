package br.com.carteira.dominio.marketplace.compra.ativos.useCase;

import br.com.carteira.dominio.DomainEventsEmiter;
import br.com.carteira.dominio.exception.DominioException;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import br.com.carteira.dominio.marketplace.compra.ativos.records.PedidoCompraDTO;

import static br.com.carteira.dominio.DomainEvent.ordemCompraEvent;
import static br.com.carteira.dominio.Utils.generateUUID_String;

public class OrdemCompraUseCases {

    private final OrdemCompraGateway ordemCompraGateway;
    private final DomainEventsEmiter eventsEmiter;

    public OrdemCompraUseCases(OrdemCompraGateway ordemCompraGateway, DomainEventsEmiter eventsEmiter) {
        this.ordemCompraGateway = ordemCompraGateway;
        this.eventsEmiter = eventsEmiter;
    }

    public void executarPedidoCompraAtivo(PedidoCompraDTO pedidoCompraDTO) {
        var ativo = ordemCompraGateway.buscarAtivo(pedidoCompraDTO.ativoRef())
                .orElseThrow();

        var ordemCompra = new OrdemCompra(
                generateUUID_String(),
                null,
                pedidoCompraDTO.quantidade(),
                pedidoCompraDTO.usuarioRef(),
                pedidoCompraDTO.ativoRef()

        );

        var possuiSaldo = ordemCompraGateway.verificarSeUsuarioPossuiSaldo(ordemCompra.getUsuarioRef(), ativo.getValorAtual());
        if (!possuiSaldo) {
            throw new DominioException("Usuário não possui saldo suficiente");
        }

        ordemCompraGateway.registrarOrdem(ordemCompra);
        ordemCompraGateway.retirarSaldoDoUsuario(ordemCompra.getUsuarioRef(), ordemCompra.getQuantidade());
        eventsEmiter.emit(ordemCompraEvent(ordemCompra));
    }
}
