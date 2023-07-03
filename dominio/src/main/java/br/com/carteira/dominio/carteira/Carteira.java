package br.com.carteira.dominio.carteira;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.AtivoComTicker;
import br.com.carteira.dominio.exception.DominioException;
import br.com.carteira.dominio.metas.Meta;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Carteira {
    private String nome;
    private Meta meta;

    private Set<Ativo> ativos;
    private String identificacao;
    private int quantidadeAtivos;

    public Carteira() {
    }

    public Set<Ativo> removeByTicker(String ticker) {
        var ativoEncontrado = ativos.stream().filter(ativo -> {
                    if (this.verificarAcaoNacional(ativo) || verificarAcaoInternacional(ativo))
                        return true;
                    else
                        return false;
                }).map(ativo -> (AcaoNacional) ativo)
                .filter(acaoNacional -> acaoNacional.getTicker().equals(ticker))
                .findFirst()
                .orElseThrow(() -> new DominioException("Ticker nao esta nessa carteira"));

        boolean remove = ativos.remove(ativoEncontrado);
        if (remove)
            return ativos;
        else
            throw new DominioException("Não foi possivel remver esse ativo");
    }

    public Set<AtivoComTicker> getAtivosComTicker() {
        return ativos.stream()
                .filter(this::verificarAtivoComTicker)
                .map(ativo -> {
                    AtivoComTicker a = (AtivoComTicker) ativo;
                    return AtivoComTicker.fromParent(a.getTicker(), ativo);
                }).collect(Collectors.toUnmodifiableSet());
    }

    private boolean verificarAtivoComTicker(Ativo ativo) {
        try {
            AtivoComTicker a = (AtivoComTicker) ativo;
            return Objects.nonNull(a.getTicker());
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public AcaoNacional getAcaoNacionalByTicker(String ticker) {
        return (AcaoNacional) ativos.stream()
                .filter(this::verificarAcaoNacional)
                .findFirst()
                .orElseThrow(() -> new DominioException("Não existe essa ação na carteira"));
    }

    public AtivoComTicker getAtivoByTicker(String ticker) {
        return (AtivoComTicker) ativos.stream()
                .filter(this::verificarAtivoComTicker)
                .findFirst()
                .orElseThrow(() -> new DominioException("Não existe essa ação na carteira"));
    }

    public AcaoInternacional getAcaoInternacionalByTicker(String ticker) {
        return (AcaoInternacional) ativos.stream()
                .filter(this::verificarAcaoInternacional)
                .findFirst()
                .orElseThrow(() -> new DominioException("Não existe essa ação na carteira"));
    }

    public Set<AcaoNacional> getAcoesNacionais() {
        return ativos.stream()
                .filter(this::verificarAcaoNacional)
                .map(ativo -> {
                    AcaoNacional acaoNacional = (AcaoNacional) ativo;
                    return AcaoNacional.fromParent(acaoNacional.getTicker(), ativo);
                }).collect(Collectors.toUnmodifiableSet());
    }

    public Set<AcaoInternacional> getAcoesInternacionais() {
        return ativos.stream()
                .filter(this::verificarAcaoInternacional)
                .map(ativo -> {
                    AcaoInternacional acao = (AcaoInternacional) ativo;
                    return AcaoInternacional.fromParent(acao.getTicker(), ativo);
                }).collect(Collectors.toUnmodifiableSet());
    }

    private boolean verificarAcaoNacional(Ativo ativo) {
        try {
            AcaoNacional acaoNacional = (AcaoNacional) ativo;
            return Objects.nonNull(acaoNacional.getTicker());
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    private boolean verificarAcaoInternacional(Ativo ativo) {
        try {
            AcaoInternacional acao = (AcaoInternacional) ativo;
            return Objects.nonNull(acao.getTicker());
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getNome() {
        return nome;
    }

    public Meta getMeta() {
        return meta;
    }

    public Set<Ativo> getAtivos() {
        return ativos;
    }

    public void setAtivos(Set<Ativo> ativos) {
        this.ativos = ativos;
    }

    public int getQuantidadeAtivos() {
        if (quantidadeAtivos == 0)
            return ativos.size();

        return quantidadeAtivos;
    }

    public void setQuantidadeAtivos(int quantidadeAtivos) {
        this.quantidadeAtivos = quantidadeAtivos;
    }
}
