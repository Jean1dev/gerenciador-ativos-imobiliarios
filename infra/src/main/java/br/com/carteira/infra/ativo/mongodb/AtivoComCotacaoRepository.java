package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AtivoComCotacaoRepository extends MongoRepository<AtivoComCotacao, String> {

    List<AtivoComCotacao> findAllByTickerIn(List<String> tickerList);

    Optional<AtivoComCotacao> findByTicker(String ticker);
}
