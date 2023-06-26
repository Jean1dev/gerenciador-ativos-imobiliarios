package br.com.carteira.infra.carteira.service;

import br.com.carteira.dominio.carteira.useCase.CriarUmaNovaCarteiraUseCase;
import br.com.carteira.dominio.carteira.useCase.records.CriarCarteiraInput;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteiraService {
    private final CriarUmaNovaCarteiraUseCase criarUmaNovaCarteiraUseCase;
    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;

    public CarteiraService(
            CriarUmaNovaCarteiraUseCase criarUmaNovaCarteiraUseCase,
            CarteiraRepository carteiraRepository, UsuarioService usuarioService) {
        this.criarUmaNovaCarteiraUseCase = criarUmaNovaCarteiraUseCase;
        this.carteiraRepository = carteiraRepository;
        this.usuarioService = usuarioService;
    }

    public void criarOuAtualizar(CriarCarteiraInput input) {
        criarUmaNovaCarteiraUseCase.executar(input);
    }

    public List<CarteiraDocument> minhasCarteiras(String userName, String email) {
        var id = usuarioService.needUsuario(userName, email).getId();
        return carteiraRepository.findByUsuarioRef(id);
    }
}
