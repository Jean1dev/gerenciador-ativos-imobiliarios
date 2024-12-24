package br.com.carteira.dominio.marketplace.compra.ativos;

import java.time.LocalDateTime;

import static br.com.carteira.dominio.Utils.nullOrValue;

public class OrdemCompra {
    private String id;
    private LocalDateTime quando;
    private Double quantidade;
    private String usuarioRef;
    private String ativoRef;

    public OrdemCompra(
            String id,
            LocalDateTime quando,
            Double quantidade,
            String usuarioRef,
            String ativoRef
    ) {
        this.id = id;
        this.quando = (LocalDateTime) nullOrValue(quando, LocalDateTime.now());
        this.quantidade = quantidade;
        this.usuarioRef = usuarioRef;
        this.ativoRef = ativoRef;
        validar();
    }

    public void validar() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id não pode ser nulo ou vazio");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (usuarioRef == null || usuarioRef.isBlank()) {
            throw new IllegalArgumentException("Usuário não pode ser nulo ou vazio");
        }
        if (ativoRef == null || ativoRef.isBlank()) {
            throw new IllegalArgumentException("Ativo não pode ser nulo ou vazio");
        }
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getQuando() {
        return quando;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public String getUsuarioRef() {
        return usuarioRef;
    }

    public String getAtivoRef() {
        return ativoRef;
    }
}
