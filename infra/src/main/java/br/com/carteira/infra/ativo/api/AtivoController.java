package br.com.carteira.infra.ativo.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.GestaoAtivosUseCase;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AportarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;
import br.com.carteira.infra.ativo.service.AtivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "ativo")
public class AtivoController {

    @Autowired
    private GestaoAtivosUseCase gestaoAtivosUseCase;
    @Autowired
    private AtivoService ativoService;

    @GetMapping("sugestao")
    public List<String> buscarSugestao(
            @RequestParam("query") String query,
            @RequestParam(value = "onlyCrypto", required = false, defaultValue = "false") boolean onlyCrypto) {
        return ativoService.suggesty(query, onlyCrypto);
    }

    @PostMapping("aportar")
    public void novoAporte(@RequestBody AportarAtivoInput body) {
        ativoService.aportar(body);
    }

    @PutMapping
    public void atualizarAtivo(@RequestBody AtualizarAtivoInput body) {
        gestaoAtivosUseCase.atualizarAtivo(body);
    }

    @PostMapping
    public void addAtivo(@RequestBody AdicionarAtivoInput body) {
        gestaoAtivosUseCase.adicionar(body);
    }

    @DeleteMapping("{ativo}")
    public void removerAtivo(@PathVariable("ativo") String ativoId) {
        gestaoAtivosUseCase.removerAtivo(ativoId);
    }

    @GetMapping("tipo-ativos")
    public Map getTipoAtivos() {
        return Map.of(
                TipoAtivo.class.getSimpleName(),
                Arrays.stream(TipoAtivo.values()).map(tipoAtivo -> Map.of(tipoAtivo.name(), tipoAtivo.descricao()))
        );
    }
}
