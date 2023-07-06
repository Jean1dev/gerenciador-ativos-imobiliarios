package br.com.carteira.infra.ativo.config;

import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.useCase.GestaoAtivosUseCase;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtivoBeansConfiguration {

    @Bean
    public GestaoAtivosUseCase gestaoAtivosUseCase(CarteiraGateway carteiraGateway, AtivosComTickerGateway ativosComTickerGateway) {
        return new GestaoAtivosUseCase(carteiraGateway, ativosComTickerGateway);
    }
}
