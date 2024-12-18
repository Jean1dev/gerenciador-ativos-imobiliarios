package br.com.carteira.infra.marketplace.compra.ativos.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdemCompraAtivoRepository extends MongoRepository<OrdemCompraAtivo, String> {
}
