package br.com.carteira.infra.admin.api;

import br.com.carteira.infra.admin.api.dto.AtualizarAtivoComCotacaoInput;
import br.com.carteira.infra.admin.service.AdminAtivosComCotacaoService;
import br.com.carteira.infra.admin.service.AdminUsuariosService;
import br.com.carteira.infra.admin.service.AtivosComProblemasService;
import br.com.carteira.infra.ativo.component.AtualizarCotacaoAtivos;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("admin")
public class AdministracaoController {

    private final AtualizarCotacaoAtivos cotacaoAtivos;
    private final AdminAtivosComCotacaoService adminAtivosComCotacaoService;
    private final AtivosComProblemasService ativosComProblemasService;
    private final AdminUsuariosService adminUsuariosService;

    public AdministracaoController(
            AtualizarCotacaoAtivos cotacaoAtivos,
            AdminAtivosComCotacaoService adminAtivosComCotacaoService,
            AtivosComProblemasService ativosComProblemasService,
            AdminUsuariosService adminUsuariosService) {
        this.cotacaoAtivos = cotacaoAtivos;
        this.adminAtivosComCotacaoService = adminAtivosComCotacaoService;
        this.ativosComProblemasService = ativosComProblemasService;
        this.adminUsuariosService = adminUsuariosService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void atualizarCotacaoAtivos() {
        cotacaoAtivos.run();
        adminUsuariosService.adicionarSaldosUsuarios();
    }

    @PostMapping("corrigir-falhas")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void corrigirFalhas() {
        ativosComProblemasService.corrigirFalhas();
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
