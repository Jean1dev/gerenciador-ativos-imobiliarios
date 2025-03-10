package br.com.carteira.infra.marketplace.compra.ativos.service;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.AtivoDosUsuariosFactory;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.ativo.service.VariacaoAtivosService;
import br.com.carteira.infra.carteira.CarteiraFactory;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListagemAtivosServiceTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));
    @Autowired
    private ListagemAtivosService service;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CarteiraRepository carteiraRepository;
    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    @Autowired
    private VariacaoAtivosService variacaoAtivosService;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @AfterEach
    public void cleanDB() {
        ativoComCotacaoRepository.deleteAll();
    }

    @Test
    @DisplayName("deve listar todos os ativos independepente do usuario")
    void listarDisponiveis() {
        setupAtivoComCotacao();
        var ativoDisponivels = service.listarDisponiveis("", "");
        assertEquals(4, ativoDisponivels.size());
    }

    @Test
    @DisplayName("deve trazer somente ativos disponiveis para o usuario")
    void listarAtivosDisponiveisUsuario() {
        setupAtivoComCotacao();
        var usuario = setupUsuario();
        setupAtivosDosUsuarios(usuario);
        var ativoDisponivels = service.listarDisponiveis(usuario.getName(), usuario.getEmail());
        assertEquals(3, ativoDisponivels.size());
    }

    private void setupAtivosDosUsuarios(Usuario usuario) {
        ativoDosUsuariosRepository.save(
                AtivoDosUsuariosFactory.simples(
                        carteiraRepository.save(CarteiraFactory.umaCarteiraParaUsuario(usuario.getId()))
                                .getId(),
                        "VALE3")
        );
    }

    private Usuario setupUsuario() {
        return usuarioService.getUsuario("test", "test-email");
    }

    private void setupAtivoComCotacao() {
        ativoComCotacaoRepository.saveAll(List.of(
                AtivoComCotacao.criarCotacao("PETR4", TipoAtivo.ACAO_NACIONAL),
                AtivoComCotacao.criarCotacao("OIBR3", TipoAtivo.ACAO_NACIONAL),
                AtivoComCotacao.criarCotacao("DASA3", TipoAtivo.ACAO_NACIONAL),
                AtivoComCotacao.criarCotacao("VALE3", TipoAtivo.ACAO_NACIONAL)
        ));
    }
}