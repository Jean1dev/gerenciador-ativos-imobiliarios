package br.com.carteira.infra.integracoes.impl;

import br.com.carteira.dominio.crypto.CryptoAtivosMapping;
import br.com.carteira.infra.integracoes.CryptoPricesApi;
import br.com.carteira.infra.integracoes.CryptoPricesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CoinMarketCap implements CryptoPricesApi {
    private static final Logger log = LoggerFactory.getLogger(CoinMarketCap.class);

    private final RestTemplate restTemplate;
    @Value("${api.coinmarketmap.key}")
    private String apiKey;
    private final String BASE_URL = "https://pro-api.coinmarketcap.com";
    private List<CryptoPricesDto> resultList;

    public CoinMarketCap() {
        restTemplate = new RestTemplate();
    }

    private CoinMarketCapResponse call() {
        try {
            var uri = String.format("%s/v1/cryptocurrency/listings/latest", BASE_URL);
            log.info("Chamando a API CoinMarketCap");
            var urlTemplate = UriComponentsBuilder.fromHttpUrl(uri)
                    .queryParam("start", "1")
                    .queryParam("limit", "3000")
                    .queryParam("convert", "USD")
                    .build()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-CMC_PRO_API_KEY", apiKey);

            var requestEntity = new RequestEntity<>(headers, HttpMethod.GET, urlTemplate);
            var response = restTemplate.exchange(requestEntity, CoinMarketCapResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error(e.getLocalizedMessage());
            log.info("Nao foi possivel atualizar a cotacao");
        }
        return null;
    }

    @Override
    public synchronized List<CryptoPricesDto> get() {
        if (Objects.nonNull(resultList) && !resultList.isEmpty()) {
            return resultList;
        }

        var namesToSearch = CryptoAtivosMapping.listMapping();
        var response = call();
        if (response == null) {
            return Collections.emptyList();
        }

        resultList = response.data().stream()
                .filter(dataResponse -> namesToSearch.contains(dataResponse.name().toUpperCase()))
                .map(dataResponse -> new CryptoPricesDto(dataResponse.name().toUpperCase(), dataResponse.quote().usd().price()))
                .toList();

        return resultList;
    }
}
