package br.com.carteira.infra.ativo.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Ativo Controller")
class AtivoControllerTest extends E2ETests {

    @Autowired
    public CarteiraRepository carteiraRepository;
    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    @DisplayName("deve adicionar uma acao")
    public void deveAdicionarUmaAcao() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste", null, null, 0)).getId();
        var ativoInput = new AdicionarAtivoInput(
                TipoAtivo.ACAO_INTERNACIONAL,
                5,
                0.0,
                "NASDAQ",
                "String tipoAlocacao",
                1.0,
                "AAPL",
                idCarteira
        );

        final var request = post("/ativo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(ativoInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, carteiraRepository.findById(idCarteira).get().getQuantidadeAtivos());
    }

    @Test
    @DisplayName("deve remover um ativo da carteira")
    public void deveRemover() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste2", null, null, 1)).getId();
        var stringTicker = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                idCarteira,
                null,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                "String ticker",
                null)).getId();

        final var request = delete("/ativo/" + stringTicker)
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email");

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var byId = ativoDosUsuariosRepository.findById(stringTicker);
        assertTrue(byId.isEmpty());
        assertEquals(0, carteiraRepository.findById(idCarteira).get().getQuantidadeAtivos());
    }

    @Test
    @DisplayName("Deve retornar todos os enums de ativos")
    public void deveRetornar() throws Exception {
        final var request = get("/ativo/tipo-ativos")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.TipoAtivo[0].ACAO_NACIONAL").value("Ações Nacionais"))
                .andExpect(jsonPath("$.TipoAtivo[1].ACAO_INTERNACIONAL").value("Ações Internacionais"))
                .andExpect(jsonPath("$.TipoAtivo[2].REITs").value("Real Estate"))
                .andExpect(jsonPath("$.TipoAtivo[3].FII").value("Fundos Imobiliarios"))
                .andExpect(jsonPath("$.TipoAtivo[4].CRYPTO").value("Cryptomoedas"))
                .andExpect(jsonPath("$.TipoAtivo[5].RENDA_FIXA").value("Renda Fixa"));
    }

}