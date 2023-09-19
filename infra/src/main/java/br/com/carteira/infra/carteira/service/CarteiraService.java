package br.com.carteira.infra.carteira.service;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.AtivoComTicker;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.useCase.CalcularPercentualCarteiraEmMetasUseCase;
import br.com.carteira.dominio.carteira.useCase.CriarEAtualizarCarteiraUserCase;
import br.com.carteira.dominio.carteira.useCase.NovoAporteUseCase;
import br.com.carteira.dominio.carteira.useCase.records.AtivoComPercentualETotal;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.dominio.carteira.useCase.records.NovoAporteOutput;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.api.MeusAtivosFilter;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.nullOrValue;

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

    public Page<AtivoDosUsuarios> meusAtivos(PageRequest pageRequest, MeusAtivosFilter filter) {
        var carteiras = filter.getCarteiras();
        var tipos = (List<String>) nullOrValue(filter.getTipos(), Collections.emptyList());
        // TODO:: so permitir buscar ativos da carteira do usuario do request

        if (tipos.isEmpty())
            return ativoDosUsuariosRepository.findAllByCarteiraRefIn(carteiras, pageRequest);

        return ativoDosUsuariosRepository.findAllByCarteiraRefInAndTipoAtivoIn(carteiras, tipos, pageRequest);
    }

    public List<CarteiraDocument> minhasCarteiras(String userName, String email) {
        var id = usuarioService.needUsuario(userName, email).getId();
        return carteiraRepository.findByUsuarioRef(id);
    }

    public void upsertCarteira(CriarOuAtualizarCarteiraInput input) {
        criarEAtualizarCarteiraUserCase.executar(input);
    }

    public Map<String, AtivoComPercentualETotal> distribuicaoPorMeta(String idCarteira) {
        return carteiraRepository.findById(idCarteira)
                .map(this::CarteiraDocumentToCarteiraFull)
                .map(carteira -> new CalcularPercentualCarteiraEmMetasUseCase()
                        .executar(carteira))
                .orElse(Map.of());
    }

    public NovoAporteOutput calcularNovoAporte(Double valor, String idCarteira) {
        return carteiraRepository.findById(idCarteira)
                .map(this::CarteiraDocumentToCarteiraFull)
                .map(carteira -> new NovoAporteUseCase().execute(valor, carteira))
                .orElseThrow();
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
