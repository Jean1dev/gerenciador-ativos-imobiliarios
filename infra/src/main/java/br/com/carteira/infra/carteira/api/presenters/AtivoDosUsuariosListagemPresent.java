package br.com.carteira.infra.carteira.api.presenters;

import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.List;

public final class AtivoDosUsuariosListagemPresent implements Serializable {
    private final String id;
    private final String carteira;
    private final String tipoAtivo;
    private final String nome;
    private final int nota;
    private final double quantidade;
    private final String image;
    private final double valorAtual;

    public AtivoDosUsuariosListagemPresent(
            String id,
            String carteira,
            String tipoAtivo,
            String nome,
            int nota,
            double quantidade,
            String image,
            double valorAtual
    ) {
        this.id = id;
        this.carteira = carteira;
        this.tipoAtivo = tipoAtivo;
        this.nome = nome;
        this.nota = nota;
        this.quantidade = quantidade;
        this.image = image;
        this.valorAtual = valorAtual;
    }

    public static AtivoDosUsuariosListagemPresent present(final AtivoDosUsuarios ativoDosUsuarios) {
        return new AtivoDosUsuariosListagemPresent(
                ativoDosUsuarios.getId(),
                ativoDosUsuarios.getCarteiraRef(),
                ativoDosUsuarios.getTipoAtivo().descricao(),
                ativoDosUsuarios.getTicker(),
                ativoDosUsuarios.getNota(),
                ativoDosUsuarios.getQuantidade(),
                ativoDosUsuarios.getImage(),
                ativoDosUsuarios.getValorAtual()
        );
    }

    public static Page<AtivoDosUsuariosListagemPresent> presents(final Page<AtivoDosUsuarios> page) {
        var presented = AtivoDosUsuariosListagemPresent.present(page.getContent());
        return new PageImpl<>(presented, page.getPageable(), page.getTotalElements());
    }

    public static List<AtivoDosUsuariosListagemPresent> present(final List<AtivoDosUsuarios> ativoDosUsuarios) {
        return ativoDosUsuarios.stream().map(AtivoDosUsuariosListagemPresent::present).toList();
    }

    public String getId() {
        return id;
    }

    public String getCarteira() {
        return carteira;
    }

    public String getTipoAtivo() {
        return tipoAtivo;
    }

    public String getNome() {
        return nome;
    }

    public int getNota() {
        return nota;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public String getImage() {
        return image;
    }

    public double getValorAtual() {
        return valorAtual;
    }
}
