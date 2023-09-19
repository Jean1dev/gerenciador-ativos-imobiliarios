package br.com.carteira.infra.ativo.api;

import br.com.carteira.infra.ativo.api.dto.QuoteResponseDTO;
import br.com.carteira.infra.ativo.service.QuoteVariationService;
import br.com.carteira.infra.exceptions.ApplicationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(path = "quote")
public class QuoteVariationController {

    private final QuoteVariationService quoteVariationService;

    public QuoteVariationController(QuoteVariationService quoteVariationService) {
        this.quoteVariationService = quoteVariationService;
    }

    @GetMapping
    public List<QuoteResponseDTO> getQuota() {
        try {
            return quoteVariationService.getQuote();
        } catch (URISyntaxException e) {
            throw new ApplicationException("Erro ao recuperar a variacao das cotas");
        }
    }
}
