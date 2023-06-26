package br.com.carteira.infra.carteira.config;

import br.com.carteira.dominio.carteira.useCase.CriarUmaNovaCarteiraUseCase;
import br.com.carteira.infra.carteira.component.DefaultCarteiraGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarteiraConfiguration {

    private final DefaultCarteiraGateway defaultCarteiraGateway;

    public CarteiraConfiguration(DefaultCarteiraGateway defaultCarteiraGateway) {
        this.defaultCarteiraGateway = defaultCarteiraGateway;
    }

    @Bean
    public CriarUmaNovaCarteiraUseCase criarUmaNovaCarteiraUseCase() {
        return new CriarUmaNovaCarteiraUseCase(defaultCarteiraGateway);
    }
}
