package br.com.carteira.infra.events;

import br.com.carteira.dominio.DomainEvent;
import br.com.carteira.dominio.DomainEventsEmiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventEmiter implements DomainEventsEmiter {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void emit(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
