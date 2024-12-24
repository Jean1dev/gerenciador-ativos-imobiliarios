package br.com.carteira.infra.marketplace.compra.ativos.api;

import br.com.carteira.dominio.marketplace.compra.ativos.records.PedidoCompraDTO;
import br.com.carteira.dominio.marketplace.compra.ativos.useCase.OrdemCompraUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "marketplace/compra-ativos")
public class CompraAtivosController {

    @Autowired
    private OrdemCompraUseCases useCases;

    @PostMapping
    public void ordemCompra(@RequestBody PedidoCompraDTO dto) {
        useCases.executarPedidoCompraAtivo(dto);
    }
}
