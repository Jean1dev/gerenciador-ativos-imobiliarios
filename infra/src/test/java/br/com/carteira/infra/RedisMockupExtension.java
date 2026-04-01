package br.com.carteira.infra;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import redis.clients.jedis.Jedis;

public class RedisMockupExtension implements BeforeAllCallback {

    @MockitoBean
    Jedis jedis;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {

    }
}
