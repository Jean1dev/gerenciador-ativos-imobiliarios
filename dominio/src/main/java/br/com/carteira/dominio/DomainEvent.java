package br.com.carteira.dominio;

import static br.com.carteira.dominio.EventTypes.ORDEM_COMPRA;

public record DomainEvent(
        String name,
        Object payload
) {

    public static DomainEvent ordemCompraEvent(Object payload) {
        return new DomainEvent(ORDEM_COMPRA, payload);
    }
}