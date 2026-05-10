package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("NovoAporte E2E")
class NovoAporteControllerTest extends E2ETests {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
        ativoDosUsuariosRepository.deleteAll();
        carteiraRepository.deleteAll();
    }

    /**
     * Fluxo completo: HTTP → Controller → Service → Domain (NovoAporteUseCase) → resposta JSON.
     *
     * Carteira com meta metasDoJeanluca e 3 ativos:
     *  - VALE3  (ACAO_NACIONAL, valorAtual=50,   nota=8, qty=1) → valor=50
     *  - BTC    (CRYPTO,        valorAtual=20,   nota=6, qty=1) → valor=20
     *  - Tesouro(RENDA_FIXA,   valorAtual=1000, nota=5, qty=1) → valor=1000
     *
     * Total=1070, aporte=1000, projetado=2070
     * Gap ACAO_NACIONAL=364, Gap CRYPTO=104.2; RENDA_FIXA gap negativo
     * Normalizado: VALE3→777.4, BTC→222.6, Tesouro→0.0
     */
    @Test
    @DisplayName("deve calcular novo aporte distribuindo por gap de classe e nota do ativo")
    void deveCalcularNovoAporteComBalanceamentoPorClasseENota() throws Exception {
        // Arrange: persiste carteira com meta definida
        var carteira = carteiraRepository.save(
                new CarteiraDocument(null, "Carteira Teste", Meta.metasDoJeanluca(), "usuario-ref", 3)
        );
        var carteiraId = carteira.getId();

        // Arrange: persiste ativos com valorAtual, nota e quantidade reais
        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.ACAO_NACIONAL, "VALE3", 50.0, 8, 1.0));
        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.CRYPTO,        "BTC",   20.0, 6, 1.0));
        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.RENDA_FIXA,    "TESOURO", 1000.0, 5, 1.0));

        // Act: POST /carteira/novo-aporte/{id} com valor do aporte
        var request = post("/carteira/novo-aporte/" + carteiraId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\": 1000}");

        // Assert
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                // deve retornar lista com 3 recomendações
                .andExpect(jsonPath("$.recomendacaoAporteList", hasSize(3)))

                // lista ordenada ascendente — posição 0: Tesouro (RENDA_FIXA acima da meta)
                .andExpect(jsonPath("$.recomendacaoAporteList[0].recomendacao").value(0.0))

                // posição 1: BTC (CRYPTO abaixo da meta, gap menor)
                .andExpect(jsonPath("$.recomendacaoAporteList[1].recomendacao").value(222.6))

                // posição 2: VALE3 (ACAO_NACIONAL abaixo da meta, gap maior)
                .andExpect(jsonPath("$.recomendacaoAporteList[2].recomendacao").value(777.4))

                // metaComValorRecomendados deve conter 6 classes (todas as do metasDoJeanluca)
                .andExpect(jsonPath("$.metaComValorRecomendados", hasSize(6)));
    }

    /**
     * Aporte com dois ativos na mesma classe → ponderação por nota.
     *
     * - VALE3 (ACAO_NACIONAL, nota=7, val=30, qty=1) → peso 0.7
     * - PETR4 (ACAO_NACIONAL, nota=3, val=20, qty=1) → peso 0.3
     * - Tesouro (RENDA_FIXA,  nota=5, val=500, qty=1) → acima da meta → 0
     *
     * Total=550, aporte=500, projetado=1050
     * Gap ACAO_NACIONAL = 1050×0.20 − 50 = 160 → toda a classe recebe 500
     *   VALE3: 500×0.7 = 350
     *   PETR4: 500×0.3 = 150
     */
    @Test
    @DisplayName("deve ponderar aporte dentro da mesma classe pela nota do ativo")
    void devePonderarAporteNaClassePelaNotaDoAtivo() throws Exception {
        var carteira = carteiraRepository.save(
                new CarteiraDocument(null, "Carteira Notas", Meta.metasDoJeanluca(), "usuario-ref", 3)
        );
        var carteiraId = carteira.getId();

        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.ACAO_NACIONAL, "VALE3",   30.0,  7, 1.0));
        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.ACAO_NACIONAL, "PETR4",   20.0,  3, 1.0));
        ativoDosUsuariosRepository.save(ativo(carteiraId, TipoAtivo.RENDA_FIXA,    "TESOURO", 500.0, 5, 1.0));

        var request = post("/carteira/novo-aporte/" + carteiraId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\": 500}");

        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recomendacaoAporteList", hasSize(3)))

                // Tesouro: RENDA_FIXA acima da meta → 0
                .andExpect(jsonPath("$.recomendacaoAporteList[0].recomendacao").value(0.0))

                // PETR4: nota menor (3/10) → recebe menos
                .andExpect(jsonPath("$.recomendacaoAporteList[1].recomendacao").value(150.0))

                // VALE3: nota maior (7/10) → recebe mais
                .andExpect(jsonPath("$.recomendacaoAporteList[2].recomendacao").value(350.0));
    }

    /**
     * Carteira sem meta definida → deve retornar erro (400/500 com mensagem de domínio).
     */
    @Test
    @DisplayName("deve retornar erro quando a carteira não tem meta definida")
    void deveRetornarErroSemMeta() throws Exception {
        var carteira = carteiraRepository.save(
                new CarteiraDocument(null, "Sem Meta", null, "usuario-ref", 1)
        );
        ativoDosUsuariosRepository.save(ativo(carteira.getId(), TipoAtivo.ACAO_NACIONAL, "VALE3", 100.0, 5, 1.0));

        var request = post("/carteira/novo-aporte/" + carteira.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"valor\": 500}");

        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // helpers

    private AtivoDosUsuarios ativo(String carteiraId, TipoAtivo tipo, String ticker,
                                    double valorAtual, int nota, double quantidade) {
        return new AtivoDosUsuarios(
                null, carteiraId, tipo,
                "",        // localAlocado
                0.0,       // percentualRecomendado
                valorAtual,
                nota,
                0.0,       // percentualTotal
                quantidade,
                ticker,
                "",        // image
                null       // criterios
        );
    }
}
