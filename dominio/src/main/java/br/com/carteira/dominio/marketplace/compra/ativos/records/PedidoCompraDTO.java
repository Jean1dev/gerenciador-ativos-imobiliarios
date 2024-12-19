package br.com.carteira.dominio.marketplace.compra.ativos.records;

public record PedidoCompraDTO(
        String ativoRef,
        String usuarioRef,
        double quantidade
) {
}