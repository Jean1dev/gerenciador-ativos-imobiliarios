package br.com.carteira.infra.marketplace.compra.ativos.config;

import br.com.carteira.dominio.marketplace.compra.ativos.useCase.OrdemCompraUseCases;
import br.com.carteira.infra.marketplace.compra.ativos.component.DefaultOrdemCompraGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdemCompraConfig {

    @Bean
    public OrdemCompraUseCases ordemCompraUseCases(DefaultOrdemCompraGateway gateway) {
        return new OrdemCompraUseCases(gateway);
    }
}
