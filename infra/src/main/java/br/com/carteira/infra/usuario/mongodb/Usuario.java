package br.com.carteira.infra.usuario.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuario")
public class Usuario {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String email;
    private Double saldo;

    public Usuario(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.saldo = 0.0;
    }

    public Usuario(String id, String name, String email, Double saldo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.saldo = saldo;
    }

    public Usuario() {
    }

    public void atualizarSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Double getSaldo() {
        return saldo;
    }
}
