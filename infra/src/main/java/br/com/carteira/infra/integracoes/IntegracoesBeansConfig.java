package br.com.carteira.infra.integracoes;

import br.com.carteira.infra.integracoes.impl.Aplhavante;
import br.com.carteira.infra.integracoes.impl.CoinMarketCap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegracoesBeansConfig {

    @Bean
    public BMFBovespa bmfBovespa() {
        return new Aplhavante();
    }

    @Bean
    public CryptoPricesApi cryptoPricesApi() {
        return new CoinMarketCap();
    }
}
