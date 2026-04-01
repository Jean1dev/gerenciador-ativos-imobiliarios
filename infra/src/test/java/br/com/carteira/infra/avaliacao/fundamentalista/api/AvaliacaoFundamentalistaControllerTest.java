package br.com.carteira.infra.avaliacao.fundamentalista.api;

import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AvaliacaoFundamentalista API")
class AvaliacaoFundamentalistaControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @Autowired
    private AvaliacaoFundamentalistaRepository repository;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @AfterEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET retorna lista vazia quando não há avaliações")
    void getListaVazia() throws Exception {
        mvc.perform(get("/avaliacao-fundamentalista").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST persiste relatório e GET retorna avaliações ordenadas por nota")
    void postRelatorioEGetRetornaOrdenadoPorNota() throws Exception {
        String body = """
                {
                  "geradoEm": "2026-03-17T20:18:01.911Z",
                  "ativos": [
                    {
                      "codigo": "XPML11",
                      "nome": "XPML11",
                      "tipoAtivo": "FII",
                      "nota": 6.0,
                      "respostas": [
                        {
                          "pergunta": "P/VP abaixo de 1?",
                          "resposta": true,
                          "justificativa": "P/VP 0,95."
                        }
                      ],
                      "dataAvaliacao": "2026-03-17T20:18:00.000Z",
                      "fontesUtilizadas": ["https://www.fundsexplorer.com.br/funds/xpml11"]
                    },
                    {
                      "codigo": "SAPR4",
                      "nome": "SAPR4",
                      "tipoAtivo": "ACAO_NACIONAL",
                      "nota": 7.0,
                      "respostas": [
                        {
                          "pergunta": "ROE historicamente maior que 5%?",
                          "resposta": true,
                          "justificativa": "ROE atual de 16,8%."
                        }
                      ],
                      "dataAvaliacao": "2026-03-17T20:17:50.292Z",
                      "fontesUtilizadas": ["https://www.fundamentus.com.br/detalhes.php?papel=sapr4"]
                    }
                  ]
                }
                """;

        mvc.perform(post("/avaliacao-fundamentalista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mvc.perform(get("/avaliacao-fundamentalista").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codigo").value("SAPR4"))
                .andExpect(jsonPath("$[0].nota").value(7.0))
                .andExpect(jsonPath("$[0].tipoAtivo").value("ACAO_NACIONAL"))
                .andExpect(jsonPath("$[0].respostas.length()").value(1))
                .andExpect(jsonPath("$[0].respostas[0].pergunta").value("ROE historicamente maior que 5%?"))
                .andExpect(jsonPath("$[0].respostas[0].resposta").value(true))
                .andExpect(jsonPath("$[0].respostas[0].justificativa").value("ROE atual de 16,8%."))
                .andExpect(jsonPath("$[0].fontesUtilizadas.length()").value(1))
                .andExpect(jsonPath("$[1].codigo").value("XPML11"))
                .andExpect(jsonPath("$[1].nota").value(6.0))
                .andExpect(jsonPath("$[1].tipoAtivo").value("FII"));
    }

    @Test
    @DisplayName("POST com relatório vazio é aceito")
    void postRelatorioVazio() throws Exception {
        String body = """
                {
                  "geradoEm": "2026-03-17T20:18:01.911Z",
                  "ativos": []
                }
                """;

        mvc.perform(post("/avaliacao-fundamentalista")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mvc.perform(get("/avaliacao-fundamentalista").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
