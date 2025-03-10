package br.com.carteira.infra;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.mock.mockito.MockBean;
import redis.clients.jedis.Jedis;

public class RedisMockupExtension implements BeforeAllCallback {

    @MockBean
    Jedis jedis;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }
}
