package br.com.carteira.infra.carteira.component;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.infra.ativo.mongodb.AtivosDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.filters.ContextHolder;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultCarteiraGateway implements CarteiraGateway {
    private static final Logger log = LoggerFactory.getLogger(DefaultCarteiraGateway.class);

    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;
    private final ContextHolder holder;

    public DefaultCarteiraGateway(
            CarteiraRepository carteiraRepository,
            UsuarioService usuarioService,
            ContextHolder holder) {
        this.carteiraRepository = carteiraRepository;
        this.usuarioService = usuarioService;
        this.holder = holder;
    }

    @Override
    public Carteira salvar(Carteira carteira) {
        Usuario usuario = usuarioService.getUsuario(holder.getUserName(), holder.getEmail());
        log.info("Usuario localizado " + usuario.getName());
        carteiraRepository.save(new CarteiraDocument(
                null,
                carteira.getNome(),
                carteira.getMeta(),
                toAtivoDocument(carteira.getAtivos()),
                usuario.getId()
        ));
        return carteira;
    }

    private Set<AtivosDocument> toAtivoDocument(Set<Ativo> ativos) {
        return ativos.stream().map(ativo -> {
            var document = new AtivosDocument(ativo.getQuantidade(), ativo.getNota());
            switch (ativo.getTipoAtivo()) {
                case ACAO_NACIONAL -> {
                    AcaoNacional acaoNacional = (AcaoNacional) ativo;
                    document.setTicker(acaoNacional.getTicker());
                }
                case ACAO_INTERNACIONAL -> {
                    AcaoInternacional acaoNacional = (AcaoInternacional) ativo;
                    document.setTicker(acaoNacional.getTicker());
                }
            }

            return document;
        }).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void consolidar(Carteira carteira) {
        log.info("consolidando carteira " + carteira.getNome());
    }
}
