package br.com.carteira.infra.integracoes.impl;

import br.com.carteira.infra.integracoes.BMFBovespa;
import br.com.carteira.infra.integracoes.CotacaoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class Aplhavante implements BMFBovespa {

    private final RestTemplate restTemplate;
    @Value("{api.vhantage.key}")
    private String vhantageApiKey;
    private final String BASE_URL = "https://www.alphavantage.co/query";

    public Aplhavante() {
        restTemplate = new RestTemplate();
    }

    @Override
    public CotacaoDto getCotacao(String ticker) {
        try {
            var headers = new HttpHeaders();
            var uri = new URI(
                    String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=W2RC2GK1GBKGHE1V", BASE_URL, ticker)
            );
            var requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
            var response = restTemplate.exchange(requestEntity, AlphavanteGetQuotaResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                AlphavanteGetQuotaResponse alphavanteGetQuotaResponse = response.getBody();

                if (alphavanteGetQuotaResponse != null
                        && alphavanteGetQuotaResponse.getGlobalQuote() != null
                        && alphavanteGetQuotaResponse.getGlobalQuote().getPrice() != null)
                    return new CotacaoDto(alphavanteGetQuotaResponse.getGlobalQuote().getPrice());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //throw new RuntimeException(e.getMessage());

        }

        return null;
    }
}
