package br.com.carteira.infra.ativo.service;

import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.api.dto.QuoteResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("QuoteVariationService")
class QuoteVariationServiceTest extends E2ETests {
    @Autowired
    private QuoteVariationService service;
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    @DisplayName("Deve trazer uma lista de cotacoes")
    public void deveTrazer() {
        assertDoesNotThrow(() -> {
            List<QuoteResponseDTO> quote = service.getQuote();
            assertFalse(quote.isEmpty());
        });
    }

}