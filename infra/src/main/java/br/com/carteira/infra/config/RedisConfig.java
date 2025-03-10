package br.com.carteira.infra.config;

import br.com.carteira.infra.config.properties.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    @Bean
    @ConfigurationProperties("redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    public Jedis jedis(RedisProperties properties) {
        Jedis jedis = new Jedis(properties.getHost(), properties.getPort(), true);
        jedis.auth(properties.getPassword());
        return jedis;
    }
}
