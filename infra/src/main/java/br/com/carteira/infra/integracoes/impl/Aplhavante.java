package br.com.carteira.infra.integracoes.impl;

import br.com.carteira.infra.integracoes.BMFBovespa;
import br.com.carteira.infra.integracoes.CotacaoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

public class Aplhavante implements BMFBovespa {
    private static final Logger log = LoggerFactory.getLogger(Aplhavante.class);

    private final RestTemplate restTemplate;
    @Value("${api.vhantage.key}")
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
                    String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", BASE_URL, ticker, vhantageApiKey)
            );
            var requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
            var response = restTemplate.exchange(requestEntity, AlphavanteGetQuotaResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                AlphavanteGetQuotaResponse alphavanteGetQuotaResponse = response.getBody();

                if (Objects.nonNull(alphavanteGetQuotaResponse.getInformation()) &&
                        !alphavanteGetQuotaResponse.getInformation().isBlank()) {
                    return CotacaoDto.fromPrice(0.0);
                }

                if (alphavanteGetQuotaResponse != null
                        && alphavanteGetQuotaResponse.getGlobalQuote() != null
                        && alphavanteGetQuotaResponse.getGlobalQuote().getPrice() != null)
                    return CotacaoDto.fromPrice(alphavanteGetQuotaResponse.getGlobalQuote().getPrice());

            }

            return CotacaoDto.fromPrice(0.0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error(e.getLocalizedMessage());
            log.info("Nao foi possivel atualizar a cotacao " + ticker);
            var message = String.format("%s -> %s -> %s", ticker, e.getLocalizedMessage(), e.getMessage());
            return new CotacaoDto(0.0, true, message);
        }
    }
}
