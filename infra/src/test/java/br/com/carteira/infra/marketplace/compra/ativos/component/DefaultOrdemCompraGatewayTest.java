package br.com.carteira.infra.marketplace.compra.ativos.component;

import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.marketplace.compra.ativos.mongodb.OrdemCompraAtivoRepository;
import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static br.com.carteira.dominio.Utils.generateUUID_String;
import static br.com.carteira.infra.ativo.AtivoFactory.umAtivoComCotacaoSalvo;
import static br.com.carteira.infra.usuario.UsuarioFactory.umUsuarioSalvo;
import static org.junit.jupiter.api.Assertions.*;

class DefaultOrdemCompraGatewayTest extends E2ETests {
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));
    @Autowired
    private DefaultOrdemCompraGateway defaultOrdemCompraGateway;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;
    @Autowired
    private OrdemCompraAtivoRepository repository;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    void registrarOrdem() {
        var usuarioRef = umUsuarioSalvo(usuarioRepository).getId();
        var ativoRef = umAtivoComCotacaoSalvo(ativoComCotacaoRepository, "PETR4", TipoAtivo.ACAO_NACIONAL).getId();
        var ordemCompra = new OrdemCompra(generateUUID_String(), null, 1.0, usuarioRef, ativoRef);
        defaultOrdemCompraGateway.registrarOrdem(ordemCompra);

        var ordemCompraAtivo = repository.findAll().get(0);
        assertNotNull(ordemCompraAtivo);
    }

    @Test
    void verificarSeUsuarioPossuiSaldo() {
        var usuarioRef = umUsuarioSalvo(usuarioRepository, "x", "y").getId();
        assertTrue(defaultOrdemCompraGateway.verificarSeUsuarioPossuiSaldo(usuarioRef, 1.0));

        var usuarioRef2 = umUsuarioSalvo(usuarioRepository, "jor", "ele").getId();
        assertFalse(defaultOrdemCompraGateway.verificarSeUsuarioPossuiSaldo(usuarioRef2, 1000.0));
    }

    @Test
    void retirarSaldoDoUsuario() {
        var usuarioRef = umUsuarioSalvo(usuarioRepository, "xwqewer", "yrere").getId();
        defaultOrdemCompraGateway.retirarSaldoDoUsuario(usuarioRef, 1.0);

        var saldo = usuarioRepository.findById(usuarioRef).orElseThrow().getSaldo();
        assertEquals(499.0, saldo);
    }

    @Test
    void buscarAtivo() {
        var ativoRef = umAtivoComCotacaoSalvo(ativoComCotacaoRepository, "PETR4", TipoAtivo.ACAO_NACIONAL).getId();
        var ativo = (AcaoNacional) defaultOrdemCompraGateway.buscarAtivo(ativoRef).orElseThrow();
        assertEquals(TipoAtivo.ACAO_NACIONAL, ativo.getTipoAtivo());
        assertEquals("PETR4", ativo.getTicker());
    }
}