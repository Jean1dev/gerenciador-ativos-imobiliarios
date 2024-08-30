package br.com.carteira.infra.ativo.service;

import br.com.carteira.infra.ativo.records.VariacaoAtivo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class VariacaoAtivosService {
    private final MongoTemplate  mongoTemplate;
    private final String collectionName = "variacao_ativos";

    public VariacaoAtivosService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void salvarVariacaoAtivo(VariacaoAtivo variacaoAtivo) {
        mongoTemplate.save(variacaoAtivo, collectionName);
    }

}
