package br.com.carteira.infra.admin.api;

import br.com.carteira.infra.ativo.component.AtualizarCotacaoAtivos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdministracaoController {

    @Autowired
    private AtualizarCotacaoAtivos cotacaoAtivos;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void atualizarCotacaoAtivos() {
        cotacaoAtivos.run();
    }
}
