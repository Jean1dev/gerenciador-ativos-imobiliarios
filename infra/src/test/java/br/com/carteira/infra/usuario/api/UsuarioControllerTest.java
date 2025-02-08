package br.com.carteira.infra.usuario.api;

import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static br.com.carteira.infra.usuario.UsuarioFactory.umUsuarioSalvo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class UsuarioControllerTest extends E2ETests {
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveFalharPqUsuarioNaoExiste() throws Exception {
        String name = "testUser";
        String email = "test@example.com";

        final var request = get("/usuarios")
                .param("name", name)
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().is4xxClientError());

    }

    @Test
    void deveRetornarUsuario() throws Exception {
        String name = "testUser";
        String email = "test@example.com";
        umUsuarioSalvo(repository, email, name);

        final var request = get("/usuarios")
                .param("name", name)
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));

    }
}