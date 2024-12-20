package br.com.carteira.infra.admin.service;

import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class AdminUsuariosService {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Async
    public void adicionarSaldosUsuarios() {
        usuarioRepository.findAll()
                .parallelStream()
                .forEach(usuario ->
                        usuarioService.adicionarSaldoNoUsuario(
                                usuario.getName(),
                                usuario.getEmail(),
                                100.0));
    }
}
