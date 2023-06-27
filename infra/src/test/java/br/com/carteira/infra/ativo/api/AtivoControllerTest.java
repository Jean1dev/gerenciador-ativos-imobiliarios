package br.com.carteira.infra.ativo.api;

import br.com.carteira.infra.E2ETests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Ativo Controller")
class AtivoControllerTest extends E2ETests {

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
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