package br.com.carteira.infra.avaliacao.fundamentalista.api;

import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaDocument;
import br.com.carteira.infra.avaliacao.fundamentalista.service.AvaliacaoFundamentalistaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("avaliacao-fundamentalista")
public class AvaliacaoFundamentalistaController {

    private final AvaliacaoFundamentalistaService service;

    public AvaliacaoFundamentalistaController(AvaliacaoFundamentalistaService service) {
        this.service = service;
    }

    @GetMapping
    public List<AvaliacaoFundamentalistaDocument> listar() {
        return service.listarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void receberRelatorio(@RequestBody RelatorioFundamentalistaDTO dto) {
        service.salvarRelatorio(dto);
    }
}
