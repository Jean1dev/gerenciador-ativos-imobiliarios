package br.com.carteira.infra.ativo.service;

import br.com.carteira.infra.E2ETests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

class AtivoServiceTest extends E2ETests {

    @Autowired
    private AtivoService service;
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @ParameterizedTest
    @DisplayName("deve sugerir crypto ativos")
    @CsvSource({
            "btc, BTC",
            "bTc, BTC",
            "eth, ETH",
            "bit, BITCOIN",
    })
    void suggesty(final String query, final String expectedTerms) {
        service.suggesty(query, true).forEach(s -> assertTrue(s.contains(expectedTerms)));
    }
}