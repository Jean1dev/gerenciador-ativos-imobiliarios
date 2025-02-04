package br.com.carteira.infra.usuario.api;

import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public Usuario get(@RequestParam(name = "name", defaultValue = "") String name,
                       @RequestParam(name = "email", defaultValue = "") String email) {
        return usuarioService.needUsuario(name, email);
    }
}
