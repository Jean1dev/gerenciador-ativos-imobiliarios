package br.com.carteira.infra.carteira.service;

import br.com.carteira.dominio.carteira.useCase.CriarEAtualizarCarteiraUserCase;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteiraService {
    private final CriarEAtualizarCarteiraUserCase criarUmaNovaCarteiraUseCase;
    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;

    public CarteiraService(
            CriarEAtualizarCarteiraUserCase criarUmaNovaCarteiraUseCase,
            CarteiraRepository carteiraRepository, UsuarioService usuarioService) {
        this.criarUmaNovaCarteiraUseCase = criarUmaNovaCarteiraUseCase;
        this.carteiraRepository = carteiraRepository;
        this.usuarioService = usuarioService;
    }

    public void upsertCarteira(CriarOuAtualizarCarteiraInput input) {
        criarUmaNovaCarteiraUseCase.executar(input);
    }

    public List<CarteiraDocument> minhasCarteiras(String userName, String email) {
        var id = usuarioService.needUsuario(userName, email).getId();
        return carteiraRepository.findByUsuarioRef(id);
    }
}
