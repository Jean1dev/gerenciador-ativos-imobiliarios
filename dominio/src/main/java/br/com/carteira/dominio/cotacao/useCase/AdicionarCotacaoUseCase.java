package br.com.carteira.dominio.cotacao.useCase;

import br.com.carteira.dominio.cotacao.Cotacao;
import br.com.carteira.dominio.cotacao.CotacaoGateway;

import java.util.Optional;

public class AdicionarCotacaoUseCase {

    private final CotacaoGateway repositoryGateway;

    public AdicionarCotacaoUseCase(CotacaoGateway repositoryGateway) {
        this.repositoryGateway = repositoryGateway;
    }

    public void adicionarCotacao(Cotacao cotacao) {
        Optional<Cotacao> papel = repositoryGateway.getByPapel(cotacao.getPapel());
        if (papel.isPresent()) {
            repositoryGateway.updateOrInsert(cotacao);
        }
    }
}
