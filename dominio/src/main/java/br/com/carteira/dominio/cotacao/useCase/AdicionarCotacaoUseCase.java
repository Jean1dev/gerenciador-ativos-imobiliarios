package br.com.carteira.dominio.cotacao.useCase;

import br.com.carteira.dominio.cotacao.Cotacao;
import br.com.carteira.dominio.cotacao.CotacaoRepositoryGateway;

import java.util.Optional;

public class AdicionarCotacaoUseCase {

    private final CotacaoRepositoryGateway repositoryGateway;

    public AdicionarCotacaoUseCase(CotacaoRepositoryGateway repositoryGateway) {
        this.repositoryGateway = repositoryGateway;
    }

    public void adicionarCotacao(Cotacao cotacao) {
        Optional<Cotacao> papel = repositoryGateway.getByPapel(cotacao.getPapel());
        if (papel.isPresent()) {
            repositoryGateway.updateOrInsert(cotacao);
        }
    }
}
