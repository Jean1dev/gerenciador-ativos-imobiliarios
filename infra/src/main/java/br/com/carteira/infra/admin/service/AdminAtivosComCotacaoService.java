package br.com.carteira.infra.admin.service;

import br.com.carteira.infra.admin.api.dto.AtualizarAtivoComCotacaoInput;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminAtivosComCotacaoService {
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;

    public AdminAtivosComCotacaoService(AtivoComCotacaoRepository ativoComCotacaoRepository) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
    }

    public AtivoComCotacao atualizar(AtualizarAtivoComCotacaoInput input) {
        var ativoComCotacao = ativoComCotacaoRepository.findByTicker(input.nome()).orElseThrow();
        var needUpdate = false;
        if (Objects.nonNull(input.valor())) {
            ativoComCotacao.atualizarValor(input.valor());
            needUpdate = true;
        }

        if (Objects.nonNull(input.imageUrl())) {
            ativoComCotacao.setImage(input.imageUrl());
            needUpdate = true;
        }

        if (needUpdate) {
            ativoComCotacaoRepository.save(ativoComCotacao);
            // consolidar carteiras que tem esse ativo
        }

        return ativoComCotacao;
    }

    public Set<String> findAllWhereImageNull() {
        return ativoComCotacaoRepository.findAllByImageNull()
                .stream()
                .map(AtivoComCotacao::getTicker)
                .collect(Collectors.toSet());
    }
}
