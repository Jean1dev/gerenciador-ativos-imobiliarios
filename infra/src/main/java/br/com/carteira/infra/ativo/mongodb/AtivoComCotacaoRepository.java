package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AtivoComCotacaoRepository extends MongoRepository<AtivoComCotacao, String> {

    List<AtivoComCotacao> findAllByTickerIn(List<String> tickerList);
}
