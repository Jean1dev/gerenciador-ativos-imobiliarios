package br.com.carteira.infra.usuario.service;

import br.com.carteira.infra.exceptions.ApplicationException;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUsuario(String name, String email) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(email);
        return usuarioRepository.findByNameAndEmail(name, email)
                .orElseGet(() -> usuarioRepository.save(new Usuario(null, name, email)));
    }

    public Usuario needUsuario(String name, String email) {
        return usuarioRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> {
                    throw new ApplicationException("Usuario não encontrado");
                });
    }

    public void adicionarSaldoNoUsuario(String name, String email, Double saldo) {
        var usuario = needUsuario(name, email);
        var novoSaldo = usuario.getSaldo() == null ? saldo : usuario.getSaldo() + saldo;
        usuario.atualizarSaldo(novoSaldo);
        usuarioRepository.save(usuario);
    }

    public void reduzirSaldoNoUsuario(String name, String email, Double saldo) {
        var usuario = needUsuario(name, email);
        var novoSaldo = usuario.getSaldo() == null ? saldo : usuario.getSaldo() + saldo;
        usuario.atualizarSaldo(novoSaldo);
        usuarioRepository.save(usuario);
    }
}
