package br.com.carteira.infra.ativo.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping(path = "ativo")
public class AtivoController {

    @GetMapping("tipo-ativos")
    public Map getTipoAtivos() {
        return Map.of(
                TipoAtivo.class.getSimpleName(),
                Arrays.stream(TipoAtivo.values()).map(tipoAtivo -> Map.of(tipoAtivo.name(), tipoAtivo.descricao()))
        );
    }
}
