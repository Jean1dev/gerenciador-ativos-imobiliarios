package br.com.carteira.infra.admin.api;

import br.com.carteira.infra.admin.api.dto.AtualizarAtivoComCotacaoInput;
import br.com.carteira.infra.admin.service.AdminAtivosComCotacaoService;
import br.com.carteira.infra.ativo.component.AtualizarCotacaoAtivos;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("admin")
public class AdministracaoController {

    @Autowired
    private AtualizarCotacaoAtivos cotacaoAtivos;
    @Autowired
    private AdminAtivosComCotacaoService adminAtivosComCotacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void atualizarCotacaoAtivos() {
        cotacaoAtivos.run();
    }

    @PutMapping("atualizar-ativo")
    public AtivoComCotacao atualizarValorOuImagem(@RequestBody AtualizarAtivoComCotacaoInput input) {
        return adminAtivosComCotacaoService.atualizar(input);
    }

    @GetMapping("ativos-sem-image")
    public Set<String> findAllSemImagem() {
        return adminAtivosComCotacaoService.findAllWhereImageNull();
    }
}
