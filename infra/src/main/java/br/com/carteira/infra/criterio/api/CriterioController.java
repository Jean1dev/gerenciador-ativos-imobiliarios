package br.com.carteira.infra.criterio.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.Criterio;
import br.com.carteira.dominio.criterios.useCase.TrazerDiagramaCorretoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("criterio")
public class CriterioController {

    @Autowired
    private TrazerDiagramaCorretoUseCase useCase;

    @GetMapping
    public List<Criterio> getPadraoByTipo(@RequestParam("tipo") TipoAtivo tipoAtivo) {
        return useCase.padrao(tipoAtivo);
    }
}
