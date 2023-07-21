package br.com.carteira.infra.admin.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.admin.api.dto.AtualizarAtivoComCotacaoInput;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Ativo Controller")
class AdministracaoControllerTest extends E2ETests {

    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    @DisplayName("deve trazer 2 ativos que estao sem imagem")
    public void deveTrazerCorretamente() throws Exception {
        ativoComCotacaoRepository.deleteAll();
        final var irbr3 = AtivoComCotacao.criarCotacao("IRBR3", TipoAtivo.ACAO_NACIONAL);
        irbr3.setImage("imagem");

        final var ativos = List.of(
                AtivoComCotacao.criarCotacao("PETR4", TipoAtivo.ACAO_NACIONAL),
                AtivoComCotacao.criarCotacao("VALE3", TipoAtivo.ACAO_NACIONAL),
                irbr3
        );
        ativoComCotacaoRepository.saveAll(ativos);

        final var request = get("/admin/ativos-sem-image")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isString())
                .andExpect(jsonPath("$[1]").isString());

        final var contentAsString = response.andReturn().getResponse().getContentAsString();
        final var list = mapper.readValue(contentAsString, List.class);
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(list.containsAll(List.of("PETR4", "VALE3")));
    }

    @Test
    void atualizarValorOuImagem() throws Exception {
        final var id = ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("PETR4", TipoAtivo.ACAO_NACIONAL))
                .getId();
        final var petr4 = new AtualizarAtivoComCotacaoInput(
                "PETR4",
                null,
                2.0
        );

        final var request = put("/admin/atualizar-ativo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(petr4));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.tipoAtivo").value(TipoAtivo.ACAO_NACIONAL.toString()))
                .andExpect(jsonPath("$.ticker").value("PETR4"))
                .andExpect(jsonPath("$.image").isEmpty())
                .andExpect(jsonPath("$.valor").value(2.0));
    }

}