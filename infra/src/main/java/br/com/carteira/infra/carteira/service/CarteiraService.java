package br.com.carteira.infra.carteira.service;

import br.com.carteira.dominio.carteira.useCase.CriarEAtualizarCarteiraUserCase;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarteiraService {
    private static final Logger log = LoggerFactory.getLogger(CarteiraService.class);
    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    private final CriarEAtualizarCarteiraUserCase criarEAtualizarCarteiraUserCase;
    private final ApplicationEventPublisher publisher;

    public CarteiraService(
            CarteiraRepository carteiraRepository,
            UsuarioService usuarioService,
            AtivoDosUsuariosRepository ativoDosUsuariosRepository,
            CriarEAtualizarCarteiraUserCase criarEAtualizarCarteiraUserCase, ApplicationEventPublisher publisher) {
        this.carteiraRepository = carteiraRepository;
        this.usuarioService = usuarioService;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
        this.criarEAtualizarCarteiraUserCase = criarEAtualizarCarteiraUserCase;
        this.publisher = publisher;
    }

    public void consolidar(String carteiraID) {
        var carteira = CarteiraDocument.simplificadoFromDocument(carteiraRepository.findById(carteiraID).orElseThrow());
        carteira.setAtivos(ativoDosUsuariosRepository.findAllByCarteiraRef(carteiraID)
                .stream().map(AtivoDosUsuarios::toAtivoComTicker)
                .collect(Collectors.toUnmodifiableSet()));

        publisher.publishEvent(carteira);
    }

    public void deletarCarteira(String carteiraId) {
        carteiraRepository.deleteById(carteiraId);
        ativoDosUsuariosRepository.deleteAllByCarteiraRef(carteiraId);
    }

    public List<AtivoDosUsuarios> meusAtivos(String carteiraRef) {
        return ativoDosUsuariosRepository.findAllByCarteiraRef(carteiraRef);
    }

    public List<CarteiraDocument> minhasCarteiras(String userName, String email) {
        var id = usuarioService.needUsuario(userName, email).getId();
        return carteiraRepository.findByUsuarioRef(id);
    }

    public void upsertCarteira(CriarOuAtualizarCarteiraInput input) {
        criarEAtualizarCarteiraUserCase.executar(input);
    }
}
