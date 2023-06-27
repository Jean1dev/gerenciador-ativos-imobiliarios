package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.carteira.useCase.records.CriarCarteiraInput;
import br.com.carteira.dominio.metas.AtivoComPercentual;
import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.dominio.metas.MetaDefinida;
import br.com.carteira.infra.carteira.api.presenters.CarteiraDocumentPresent;
import br.com.carteira.infra.carteira.service.CarteiraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("carteira")
public class CarteiraController {

    private final CarteiraService service;

    public CarteiraController(CarteiraService service) {
        this.service = service;
    }

    @PostMapping
    public void criarCarteira(@RequestBody CriarCarteiraInput input) {
        service.criarOuAtualizar(input);
    }

    @GetMapping
    public List<CarteiraDocumentPresent> getMinhasCarteiras(
            @RequestParam("user") String user,
            @RequestParam("email") String email) {
        return CarteiraDocumentPresent.present(service.minhasCarteiras(user, email));
    }

    @GetMapping("meta")
    public Set<AtivoComPercentual> getMetaByTipo(@RequestParam("tipo") MetaDefinida metaDefinida) {
        return switch (metaDefinida) {
            case META_DO_JEAN -> Meta.metasDoJeanluca().getAtivoComPercentuals();
            case CONSERVADOR -> Meta.conservador().getAtivoComPercentuals();
            case MODERADO -> Meta.moderado().getAtivoComPercentuals();
            case ARROJADO -> Meta.arrojado().getAtivoComPercentuals();
            case DINAMICO -> Meta.dinamico().getAtivoComPercentuals();
            case SOFISTICADO -> Meta.sofisticado().getAtivoComPercentuals();
            default -> null;
        };
    }

}
