package br.com.carteira.infra.carteira.service;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.AtivoComTicker;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.useCase.CalcularPercentualCarteiraEmMetasUseCase;
import br.com.carteira.dominio.carteira.useCase.CriarEAtualizarCarteiraUserCase;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CarteiraService {
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

    public Map distribuicaoPorMeta(String idCarteira) {
        return carteiraRepository.findById(idCarteira)
                .map(this::CarteiraDocumentToCarteiraFull)
                .map(carteira -> new CalcularPercentualCarteiraEmMetasUseCase()
                        .executar(carteira))
                .orElse(Map.of());
    }

    private Carteira CarteiraDocumentToCarteiraFull(CarteiraDocument carteiraDocument) {
        var carteira = CarteiraDocument.simplificadoFromDocument(carteiraDocument);
        var ativoDosUsuarios = ativoDosUsuariosRepository.findAllByCarteiraRef(carteiraDocument.getId());
        carteira.setAtivos(toAtivosCompleto(ativoDosUsuarios));
        return carteira;
    }

    private Set<Ativo> toAtivosCompleto(List<AtivoDosUsuarios> ativoDosUsuarios) {
        return ativoDosUsuarios.stream().map(ativoDosUsuario -> {
            return new AtivoComTicker(
                    ativoDosUsuario.getTicker(),
                    ativoDosUsuario.getTipoAtivo(),
                    ativoDosUsuario.getLocalAlocado(),
                    ativoDosUsuario.getPercentualRecomendado(),
                    ativoDosUsuario.getValorAtual(),
                    ativoDosUsuario.getNota(),
                    ativoDosUsuario.getPercentualTotal(),
                    ativoDosUsuario.getQuantidade()
            );
        }).collect(Collectors.toSet());
    }
}
