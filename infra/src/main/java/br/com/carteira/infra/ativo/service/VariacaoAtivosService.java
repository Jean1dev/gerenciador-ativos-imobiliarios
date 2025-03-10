package br.com.carteira.infra.ativo.service;

import br.com.carteira.infra.ativo.records.VariacaoAtivo;
import br.com.carteira.infra.cache.CacheService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static br.com.carteira.infra.utils.Utils.emptyVariationWithTicker;

@Service
public class VariacaoAtivosService {
    private final MongoTemplate mongoTemplate;
    private final CacheService cacheService;
    private final String collectionName = "variacao_ativos";

    public VariacaoAtivosService(MongoTemplate mongoTemplate, CacheService cacheService) {
        this.mongoTemplate = mongoTemplate;
        this.cacheService = cacheService;
    }

    public void salvarVariacaoAtivo(VariacaoAtivo variacaoAtivo) {
        mongoTemplate.save(variacaoAtivo, collectionName);
    }

    public VariacaoAtivo getVariacao(String ticker) {
        var dataCached = cacheService.get(getCacheKey(ticker), VariacaoAtivo.class);
        if (Objects.isNull(dataCached))
            return retrieveFromMongo(ticker);

        return (VariacaoAtivo) dataCached;
    }

    private VariacaoAtivo retrieveFromMongo(String ticker) {
        var entity = mongoTemplate.findOne(
                new Query(Criteria
                        .where("ticker")
                        .is(ticker))
                        .limit(1)
                        .with(Sort.by(Sort.Direction.DESC, "_id")),
                VariacaoAtivo.class,
                collectionName
        );

        if (Objects.isNull(entity))
            return emptyVariationWithTicker(ticker);

        cacheService.store(getCacheKey(ticker), entity);
        return entity;
    }

    public String getCacheKey(String ticker) {
        return "%s_cache".formatted(ticker);
    }
}
