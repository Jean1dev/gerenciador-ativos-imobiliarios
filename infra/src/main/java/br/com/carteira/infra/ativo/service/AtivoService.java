package br.com.carteira.infra.ativo.service;

import br.com.carteira.dominio.ativo.useCase.records.AportarAtivoInput;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.springframework.stereotype.Service;

@Service
public class AtivoService {

    private final AtivoDosUsuariosRepository repository;

    public AtivoService(AtivoDosUsuariosRepository repository) {
        this.repository = repository;
    }

    public void aportar(AportarAtivoInput input) {
        repository.findById(input.id())
                .ifPresent(ativoDosUsuarios -> {
                    ativoDosUsuarios.aporte(input.quantidade());
                    repository.save(ativoDosUsuarios);
                });
    }
}
