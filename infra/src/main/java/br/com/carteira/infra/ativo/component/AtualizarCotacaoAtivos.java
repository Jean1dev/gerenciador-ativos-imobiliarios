package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableAsync
public class AtualizarCotacaoAtivos {

    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AtualizarCotacaoAtivos(AtivoComCotacaoRepository ativoComCotacaoRepository,
                                  ApplicationEventPublisher eventPublisher) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Async
    public void run() {
        ativoComCotacaoRepository.findAllByTipoAtivoIn(List.of(TipoAtivo.ACAO_INTERNACIONAL, TipoAtivo.ACAO_NACIONAL))
                .stream()
                .filter(this::deveAtualizar)
                .parallel()
                .forEach(eventPublisher::publishEvent);
    }


    private boolean deveAtualizar(AtivoComCotacao ativoComCotacao) {
        if (ativoComCotacao.getValor() == 0) {
            return true;
        }

        return LocalDate.now().isAfter(ativoComCotacao.getUltimaAtualizacao().toLocalDate());
    }
}
