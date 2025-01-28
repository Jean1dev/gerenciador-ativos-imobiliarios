package br.com.carteira.dominio;

public interface DomainEventsEmiter {
    void emit(DomainEvent event);
}
