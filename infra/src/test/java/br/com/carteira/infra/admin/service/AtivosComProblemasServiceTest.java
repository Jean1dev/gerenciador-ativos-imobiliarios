package br.com.carteira.infra.admin.service;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.admin.records.AtivosComProblema;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AtivosComProblemasServiceTest")
class AtivosComProblemasServiceTest extends E2ETests {

    @Autowired
    private AtivosComProblemasService service;
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    void evidenciar() {
        var xpml11 = "XPML11";
        service.evidenciar(xpml11);

        List<AtivosComProblema> all = mongoOperations.findAll(AtivosComProblema.class, AtivosComProblemasService.ATIVOS_COM_PROBLEMAS_EVIDENCIAS);

        assertEquals(1, all.size());
        assertEquals(xpml11, all.get(0).ticker());
        mongoOperations.dropCollection(AtivosComProblemasService.ATIVOS_COM_PROBLEMAS_EVIDENCIAS);
    }

    @Test
    void deleteDeletarTodasAsOcorrencias() {
        var deveDeltarEsse = "PETR4";
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao(deveDeltarEsse, TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("PETR3", TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("KLBN11", TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("XPML11", TipoAtivo.FII));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("AAPL", TipoAtivo.ACAO_INTERNACIONAL));

        var idAtivo = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                "idCarteira-fake",
                null,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                deveDeltarEsse,
                null,
                null)).getId();

        service.evidenciar(deveDeltarEsse);
        service.corrigirFalhas();

        assertTrue(ativoDosUsuariosRepository.findById(idAtivo).isEmpty());
        List<AtivoComCotacao> cotacaoList = ativoComCotacaoRepository.findAll();

        assertEquals(4, cotacaoList.size());

        boolean empty = cotacaoList.stream().filter(ativoComCotacao -> ativoComCotacao.getTicker().equals(deveDeltarEsse))
                .findFirst()
                .isEmpty();

        assertTrue(empty);

        boolean collectionExists = mongoOperations.collectionExists(AtivosComProblemasService.ATIVOS_COM_PROBLEMAS_EVIDENCIAS);
        assertFalse(collectionExists);
    }
}