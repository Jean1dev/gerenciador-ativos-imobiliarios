package br.com.carteira.infra.usuario.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByNameAndEmail(String name, String email);
}
