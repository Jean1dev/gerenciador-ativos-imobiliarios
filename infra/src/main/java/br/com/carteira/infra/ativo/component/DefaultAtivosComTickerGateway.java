package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAtivosComTickerGateway implements AtivosComTickerGateway {

    private final AtivoComCotacaoRepository ativoComCotacaoRepository;

    public DefaultAtivosComTickerGateway(AtivoComCotacaoRepository ativoComCotacaoRepository) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
    }

    @Override
    public Optional<Ativo> buscarPeloTicker(String ticker) {
        Optional<AtivoComCotacao> byTicker = ativoComCotacaoRepository.findByTicker(ticker);
        if (byTicker.isEmpty())
            return Optional.empty();

        return byTicker.map(AtivoComCotacao::fromDocument);
    }

    @Override
    public void adicionarParaMonitoramento(String ticker, TipoAtivo tipoAtivo) {
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao(ticker, tipoAtivo));
    }
}
