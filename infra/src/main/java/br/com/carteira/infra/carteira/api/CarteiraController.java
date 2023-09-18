package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.dominio.carteira.useCase.records.NovoAporteOutput;
import br.com.carteira.dominio.metas.AtivoComPercentual;
import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.dominio.metas.MetaDefinida;
import br.com.carteira.infra.carteira.api.presenters.AtivoDosUsuariosListagemPresent;
import br.com.carteira.infra.carteira.api.presenters.CarteiraDocumentPresent;
import br.com.carteira.infra.carteira.service.CarteiraService;
import br.com.carteira.infra.exceptions.ApplicationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("carteira")
public class CarteiraController {

    private final CarteiraService service;

    public CarteiraController(CarteiraService service) {
        this.service = service;
    }

    @GetMapping("distribuicao-por-meta/{carteira}")
    public Map getDistribuicao(@PathVariable("carteira") String carteira) {
        HashMap<String, Double> remap = new HashMap<>();
        service.distribuicaoPorMeta(carteira).forEach((key, value) -> remap.put(key, value.percentual()));
        return remap;
    }

    @PostMapping("novo-aporte/{carteira}")
    public NovoAporteOutput calcularNovoAporte(@RequestBody JsonNode body, @PathVariable("carteira") String carteira) {
        double valor = body.get("valor").asDouble();
        return service.calcularNovoAporte(valor, carteira);
    }

    @PostMapping("consolidar/{carteira}")
    public void consolidar(@PathVariable("carteira") String carteiraId) {
        service.consolidar(carteiraId);
    }

    @DeleteMapping("{carteira}")
    public void delete(@PathVariable("carteira") String carteiraId) {
        service.deletarCarteira(carteiraId);
    }

    @GetMapping("meus-ativos")
    public Page<AtivoDosUsuariosListagemPresent> meusAtivos(
            MeusAtivosFilter filter,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ) {
        if (Objects.isNull(filter.getCarteiras()) || filter.getCarteiras().isEmpty()) {
            throw new ApplicationException("MeusAtivosFilter esta incorreto");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        return AtivoDosUsuariosListagemPresent.presents(service.meusAtivos(pageRequest, filter));
    }

    @PostMapping
    public void criarCarteira(@RequestBody CriarOuAtualizarCarteiraInput input) {
        service.upsertCarteira(input);
    }

    @PutMapping("{id}")
    public void atualizarCarteira(@PathVariable("id") String id, @RequestBody CriarOuAtualizarCarteiraInput input) {
        service.upsertCarteira(new CriarOuAtualizarCarteiraInput(
                input.nome(),
                input.meta(),
                input.ativos(),
                input.metaDefinida(),
                id
        ));
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
