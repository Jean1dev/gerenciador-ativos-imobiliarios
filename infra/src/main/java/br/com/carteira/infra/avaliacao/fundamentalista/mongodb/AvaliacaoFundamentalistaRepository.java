package br.com.carteira.infra.avaliacao.fundamentalista.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AvaliacaoFundamentalistaRepository extends MongoRepository<AvaliacaoFundamentalistaDocument, String> {

    List<AvaliacaoFundamentalistaDocument> findAllByOrderByNotaDesc();
}
