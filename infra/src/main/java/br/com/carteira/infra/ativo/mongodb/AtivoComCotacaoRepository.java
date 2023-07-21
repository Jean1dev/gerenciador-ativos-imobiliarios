package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AtivoComCotacaoRepository extends MongoRepository<AtivoComCotacao, String> {

    List<AtivoComCotacao> findAllByTickerIn(List<String> tickerList);

    Optional<AtivoComCotacao> findByTicker(String ticker);

    @Query("{'image': null}")
    List<AtivoComCotacao> findAllByImageNull();
}
