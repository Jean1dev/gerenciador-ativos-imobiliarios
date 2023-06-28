package br.com.carteira.infra.integracoes.impl;

import br.com.carteira.infra.integracoes.BMFBovespa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class Aplhavante implements BMFBovespa {

    private final RestTemplate restTemplate;
    @Value("{api.vhantage.key}")
    private String vhantageApiKey;

    public Aplhavante() {
        restTemplate = new RestTemplate();
    }
}
