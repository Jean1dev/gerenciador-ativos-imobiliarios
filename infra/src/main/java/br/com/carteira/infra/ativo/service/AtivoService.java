package br.com.carteira.infra.ativo.service;

import br.com.carteira.dominio.ativo.useCase.records.AportarAtivoInput;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtivoService {

    private final AtivoDosUsuariosRepository repository;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;

    public AtivoService(AtivoDosUsuariosRepository repository, AtivoComCotacaoRepository ativoComCotacaoRepository) {
        this.repository = repository;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
    }

    public List<String> suggesty(String query) {
        return ativoComCotacaoRepository.findAllByTickerContaining(query.toUpperCase())
                .stream().map(AtivoComCotacao::getTicker)
                .toList();
    }

    public void aportar(AportarAtivoInput input) {
        repository.findById(input.id())
                .ifPresent(ativoDosUsuarios -> {
                    ativoDosUsuarios.aporte(input.quantidade());
                    repository.save(ativoDosUsuarios);
                });
    }
}
