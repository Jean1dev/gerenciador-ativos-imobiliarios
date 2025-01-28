package br.com.carteira.infra.ativo;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;

public final class AtivoFactory {

    public static AtivoComCotacao umAtivoComCotacaoSalvo(AtivoComCotacaoRepository repository, String ticker, TipoAtivo tipoAtivo) {
        return repository.save(
                AtivoComCotacao.criarCotacao(ticker, tipoAtivo)
        );
    }
}
