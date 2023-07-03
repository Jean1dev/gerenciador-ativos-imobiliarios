package br.com.carteira.infra.ativo.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.GestaoAtivosUseCase;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(path = "ativo")
public class AtivoController {

    @Autowired
    public GestaoAtivosUseCase gestaoAtivosUseCase;

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
