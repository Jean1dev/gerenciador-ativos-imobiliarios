package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultAtivosComTickerGateway implements AtivosComTickerGateway {

    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    public DefaultAtivosComTickerGateway(AtivoComCotacaoRepository ativoComCotacaoRepository, AtivoDosUsuariosRepository ativoDosUsuariosRepository) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
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

    @Override
    public void atualizarAtivo(String ativoId, Integer nota, Double quantidade) {
        var ativoDosUsuarios = ativoDosUsuariosRepository.findById(ativoId).orElseThrow();
        ativoDosUsuarios.setNota(nota);
        ativoDosUsuarios.setQuantidade(quantidade);
        ativoDosUsuariosRepository.save(ativoDosUsuarios);
    }
}