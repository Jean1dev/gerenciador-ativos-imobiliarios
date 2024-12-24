package br.com.carteira.infra.marketplace.compra.ativos.api;

import br.com.carteira.infra.marketplace.compra.ativos.records.AtivoDisponivel;
import br.com.carteira.infra.marketplace.compra.ativos.service.ListagemAtivosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "marketplace/listagem-ativos")
public class ListagemAtivosController {
    @Autowired
    private ListagemAtivosService service;

    @GetMapping
    public List<AtivoDisponivel> listar(
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "email", required = false) String email
    ) {
        return service.listarDisponiveis(user, email);
    }
}
