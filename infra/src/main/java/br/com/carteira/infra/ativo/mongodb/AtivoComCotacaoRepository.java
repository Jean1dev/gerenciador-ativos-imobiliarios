package br.com.carteira.infra.ativo.mongodb;

import br.com.carteira.dominio.ativo.TipoAtivo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AtivoComCotacaoRepository extends MongoRepository<AtivoComCotacao, String> {

    List<AtivoComCotacao> findAllByTickerIn(List<String> tickerList);

    List<AtivoComCotacao> findAllByTickerContaining(String query);

    Optional<AtivoComCotacao> findByTicker(String ticker);

    @Query("{'image': null}")
    List<AtivoComCotacao> findAllByImageNull();

    List<AtivoComCotacao> findAllByTipoAtivoIn(List<TipoAtivo> tipoAtivos);
}
