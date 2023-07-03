package br.com.carteira.infra.carteira.component;

import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.filters.ContextHolder;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultCarteiraGateway implements CarteiraGateway {
    private static final Logger log = LoggerFactory.getLogger(DefaultCarteiraGateway.class);
    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;
    private final ContextHolder holder;
    private final ApplicationEventPublisher publisher;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    public DefaultCarteiraGateway(
            CarteiraRepository carteiraRepository,
            UsuarioService usuarioService,
            ContextHolder holder,
            ApplicationEventPublisher publisher,
            AtivoDosUsuariosRepository ativoDosUsuariosRepository) {
        this.carteiraRepository = carteiraRepository;
        this.usuarioService = usuarioService;
        this.holder = holder;
        this.publisher = publisher;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
    }

    @Override
    public Carteira salvar(Carteira carteira) {
        Usuario usuario = usuarioService.getUsuario(holder.getUserName(), holder.getEmail());
        log.info("Usuario localizado " + usuario.getName());
        CarteiraDocument carteiraDocument = carteiraRepository.save(new CarteiraDocument(
                carteira.getIdentificacao(),
                carteira.getNome(),
                carteira.getMeta(),
                usuario.getId(),
                carteira.getQuantidadeAtivos()));
        carteira.setIdentificacao(carteiraDocument.getId());
        return carteira;
    }

    @Override
    public void consolidar(Carteira carteira) {
        if (Objects.isNull(carteira.getIdentificacao())) {
            log.info("carteira sem identição não pode ser consolidada");
            return;
        }

        publisher.publishEvent(carteira);
    }

    @Override
    public Carteira buscarCarteiraPeloId(String id) {
        return carteiraRepository.findById(id)
                .map(CarteiraDocument::simplificadoFromDocument).orElseThrow();
    }

    @Override
    public boolean verificarSeJaExisteTickerNaCarteira(Carteira carteira, String ticker) {
        return ativoDosUsuariosRepository.findByCarteiraRefAndTicker(carteira.getIdentificacao(), ticker)
                .isPresent();
    }

    @Override
    public void adicionarAtivoNaCarteira(Carteira carteira, AtivoSimplificado ativoSimplificado) {
        ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                carteira.getIdentificacao(),
                ativoSimplificado.tipoAtivo(),
                ativoSimplificado.papel(),
                0,
                0,
                ativoSimplificado.nota(),
                0,
                ativoSimplificado.quantidade(),
                ativoSimplificado.papel()
        ));
    }
}
