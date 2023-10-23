package br.com.carteira.infra.ativo.service;

import br.com.carteira.infra.ativo.api.dto.QuoteResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class QuoteVariationService {

    @Value("${api.vhantage.key}")
    private String vhantageApiKey;

    public List<QuoteResponseDTO> getQuote() throws URISyntaxException {
        var headers = new HttpHeaders();
        var uri = new URI("https://www.alphavantage.co/query?function=TOP_GAINERS_LOSERS&apikey=" + vhantageApiKey);
        var requestEntity = new RequestEntity<>(headers, HttpMethod.GET, uri);
        var response = new RestTemplate().exchange(requestEntity, JsonNode.class);
        JsonNode responseBody = response.getBody();

        var list = new ArrayList<QuoteResponseDTO>();
        var top_gainers = responseBody.get("top_gainers");
        var top_losers = responseBody.get("top_losers");
        var most_actively_traded = responseBody.get("most_actively_traded");

        populate(top_gainers.elements(), list);
        populate(top_losers.elements(), list);
        populate(most_actively_traded.elements(), list);
        return list;
    }

    private void populate(Iterator<JsonNode> elements, ArrayList<QuoteResponseDTO> list) {
        while (elements.hasNext()) {
            JsonNode nexted = elements.next();
            list.add(new QuoteResponseDTO(
                    nexted.get("ticker").asText(),
                    nexted.get("change_amount").asDouble() > 0 ? "+" : "-",
                    nexted.get("change_amount").asDouble()
            ));
        }
    }
}
