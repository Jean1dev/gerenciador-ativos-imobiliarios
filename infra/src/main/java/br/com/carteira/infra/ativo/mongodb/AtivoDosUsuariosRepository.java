package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AtivoDosUsuariosRepository extends PagingAndSortingRepository<AtivoDosUsuarios, String>,
        MongoRepository<AtivoDosUsuarios, String> {
    Optional<AtivoDosUsuarios> findByCarteiraRefAndTicker(String carteiraRef, String ticker);

    List<AtivoDosUsuarios> findAllByCarteiraRef(String carteiraRef);

    void deleteAllByCarteiraRef(String carteiraRef);

    Page<AtivoDosUsuarios> findAllByCarteiraRefInAndTipoAtivoIn(List<String> carteiras, List<String> tipos, Pageable page);

    Page<AtivoDosUsuarios> findAllByCarteiraRefIn(List<String> carteiras, Pageable page);
}
