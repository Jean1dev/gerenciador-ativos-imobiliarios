package br.com.carteira.infra.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    @Autowired
    private Jedis jedis;
    @Autowired
    private ObjectMapper mapper;

    public Object get(String key, Class<?> bindClass) {
        var cachedContent = jedis.get(key);
        if (cachedContent == null)
            return null;

        try {
            log.info("Recuperando chave do cache: {}", key);
            return mapper.readValue(cachedContent, bindClass);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String store(String key, Object data) {
        log.info("Salvando chave no cache: {}", key);
        try {
            return jedis.set(key, mapper.writeValueAsString(data), buildSetParams());
        } catch (JsonProcessingException e) {
            return "NOT_SAVED";
        }
    }

    private SetParams buildSetParams() {
        return new SetParams().ex(2 * 24 * 60 * 60); // 2 dias em segundos
    }
}
