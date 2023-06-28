package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.dominio.metas.MetaDefinida;
import br.com.carteira.infra.E2ETests;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Cateira test")
class CarteiraControllerTest extends E2ETests {

    @Autowired
    private MongoOperations mongoOperations;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @Test
    @DisplayName("deve criar uma carteira")
    public void deveCriar() throws Exception {
        var input = CriarOuAtualizarCarteiraInput.byName("nome");
        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve criar uma carteira com payload completo")
    public void deveCriarCompleto() throws Exception {
        Collection<AtivoSimplificado> ativos = List.of(
                new AtivoSimplificado(TipoAtivo.ACAO_NACIONAL, "PETR4", 5, 4),
                new AtivoSimplificado(TipoAtivo.ACAO_INTERNACIONAL, "aapl4", 5, 4)
        );
        var input = new CriarOuAtualizarCarteiraInput("Teste", null, ativos, MetaDefinida.META_DO_JEAN, null);
        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve atualizar uma carteira")
    public void deveAtualizarUmaCarteira() throws Exception {
        mongoOperations.getCollectionNames()
                .forEach(collection -> mongoOperations.dropCollection(collection));

        Collection<AtivoSimplificado> ativos = List.of(
                new AtivoSimplificado(TipoAtivo.ACAO_NACIONAL, "PETR4", 5, 4)
        );
        var input = new CriarOuAtualizarCarteiraInput("Teste", null, ativos, null, null);

        this.mvc.perform(post("/carteira")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("user", "user")
                        .header("email", "email")
                        .content(mapper.writeValueAsString(input)))
                .andDo(print());

        String contentAsString = this.mvc.perform(get("/carteira")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("user", "user")
                        .param("email", "email"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", instanceOf(String.class)))
                .andExpect(jsonPath("$[0].nome").value("Teste"))
                .andExpect(jsonPath("$[0].quantidadeAtivos").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = mapper.readValue(contentAsString, JsonNode.class);
        String id = jsonNode.get(0).get("id").asText();
        var inputUpdate = new CriarOuAtualizarCarteiraInput("atualizado", null, ativos, null, null);

        final var request = put("/carteira/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(inputUpdate));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());

        this.mvc.perform(get("/carteira")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("user", "user")
                        .param("email", "email"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", instanceOf(String.class)))
                .andExpect(jsonPath("$[0].nome").value("atualizado"))
                .andExpect(jsonPath("$[0].quantidadeAtivos").value(1));
    }

    @Test
    @DisplayName("deve buscar minhas carteiras")
    public void deveBuscarMinhasCarteiras() throws Exception {
        var input = CriarOuAtualizarCarteiraInput.byName("nome");

        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        this.mvc.perform(request).andDo(print());

        final var minhasCarteirasRequest = get("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .param("user", "user")
                .param("email", "email");

        final var response = this.mvc.perform(minhasCarteirasRequest)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", instanceOf(String.class)))
                .andExpect(jsonPath("$[0].nome").value("nome"))
                .andExpect(jsonPath("$[0].quantidadeAtivos").value(0));
    }

}