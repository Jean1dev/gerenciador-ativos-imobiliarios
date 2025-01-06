package br.com.carteira.infra.listener;

import br.com.carteira.dominio.DomainEvent;
import br.com.carteira.dominio.ativo.useCase.GestaoAtivosUseCase;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.service.CarteiraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static br.com.carteira.infra.carteira.mongodb.CarteiraDocument.simplificadoFromDocument;

@Component
public class OrdemCompraListener {
    private static final Logger log = LoggerFactory.getLogger(OrdemCompraListener.class);
    @Autowired
    private CarteiraService carteiraService;
    @Autowired
    private GestaoAtivosUseCase gestaoAtivosUseCase;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;
    @Autowired
    private CarteiraGateway carteiraGateway;
    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    @EventListener
    public void on(DomainEvent event) {
        log.info("Evento recebido " + event.name());
        var ordemCompra = (OrdemCompra) event.payload();
        var carteira = simplificadoFromDocument(carteiraService.carteiraDefault(ordemCompra.getUsuarioRef()));
        var ativoComCotacao = ativoComCotacaoRepository.findById(ordemCompra.getAtivoRef()).orElseThrow();

        boolean existe = carteiraGateway.verificarSeJaExisteTickerNaCarteira(carteira, ativoComCotacao.getTicker());
        if (existe) {
            AtivoDosUsuarios ativoDosUsuarios = ativoDosUsuariosRepository.findByCarteiraRefAndTicker(carteira.getIdentificacao(), ativoComCotacao.getTicker())
                    .orElseThrow();
            gestaoAtivosUseCase.atualizarAtivo(new AtualizarAtivoInput(
                    ativoDosUsuarios.getQuantidade() + ordemCompra.getQuantidade(),
                    ativoDosUsuarios.getNota(),
                    ativoDosUsuarios.getId(),
                    ativoDosUsuarios.getCriterios()
            ));

            return;
        }

        var input = new AdicionarAtivoInput(
                ativoComCotacao.getTipoAtivo(),
                5,
                ativoComCotacao.getValor(),
                "b3",
                "fundo acao b3",
                1.0,
                ativoComCotacao.getTicker(),
                carteira.getIdentificacao(),
                Collections.emptyList()
        );
        gestaoAtivosUseCase.adicionar(input);
    }
}
