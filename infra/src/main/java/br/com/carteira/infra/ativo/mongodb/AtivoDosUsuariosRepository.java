package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AtivoDosUsuariosRepository extends MongoRepository<AtivoDosUsuarios, String> {
    Optional<AtivoDosUsuarios> findByCarteiraRefAndTicker(String carteiraRef, String ticker);

    List<AtivoDosUsuarios> findAllByCarteiraRef(String carteiraRef);
}
