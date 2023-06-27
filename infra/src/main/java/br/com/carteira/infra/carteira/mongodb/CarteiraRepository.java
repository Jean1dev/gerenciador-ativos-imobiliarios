package br.com.carteira.infra.carteira.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CarteiraRepository extends MongoRepository<CarteiraDocument, String> {
    List<CarteiraDocument> findByUsuarioRef(String usarioRef);
}
