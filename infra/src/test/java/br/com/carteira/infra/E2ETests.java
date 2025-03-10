package br.com.carteira.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.Jedis;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@Tag("e2e-test")
public abstract class E2ETests {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper mapper;
    @MockBean
    Jedis jedis;
}
