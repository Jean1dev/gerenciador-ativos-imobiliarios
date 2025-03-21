package br.com.carteira.infra.usuario;

import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;

public final class UsuarioFactory {
    public static Usuario umUsuarioSalvo(UsuarioRepository repository) {
        return repository.save(
                new Usuario(
                        null,
                        "Fulano",
                        "email",
                        500.00
                )
        );
    }

    public static Usuario umUsuarioSalvo(UsuarioRepository repository, String email, String nome) {
        return repository.save(
                new Usuario(
                        null,
                        nome,
                        email,
                        500.00
                )
        );
    }
}
