package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.carteira.useCase.records.CriarCarteiraInput;
import br.com.carteira.infra.carteira.api.presenters.CarteiraDocumentPresent;
import br.com.carteira.infra.carteira.service.CarteiraService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
