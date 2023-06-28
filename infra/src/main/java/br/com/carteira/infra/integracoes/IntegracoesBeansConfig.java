package br.com.carteira.infra.integracoes;

import br.com.carteira.infra.integracoes.impl.Aplhavante;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegracoesBeansConfig {

    @Bean
    public BMFBovespa bmfBovespa() {
        return new Aplhavante();
    }
}
