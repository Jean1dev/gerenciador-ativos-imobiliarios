package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.exception.DominioException;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.carteira.dominio.Utils.nullOrValue;

public abstract class Ativo {
    private TipoAtivo tipoAtivo;
    private String localAlocado;
    private double percentualRecomendado;
    private double valorAtual;
    private Integer nota;
    private double percentualTotal;
    private double quantidade;

    public Ativo(
            TipoAtivo tipoAtivo,
            String localAlocado,
            double percentualRecomendado,
            double valorAtual,
            int nota,
            double percentualTotal,
            double quantidade
    ) throws DominioException {
        this.tipoAtivo = Objects.requireNonNull(tipoAtivo, "Tipo ativo é obrigatorio");
        this.localAlocado = localAlocado;
        this.percentualRecomendado = (double) nullOrValue(percentualRecomendado, 0.0);
        this.valorAtual = (double) nullOrValue(valorAtual, 0.0);
        this.nota = (Integer) nullOrValue(nota, 0);
        this.percentualTotal = (double) nullOrValue(percentualTotal, 0.0);
        this.quantidade = (double) nullOrValue(quantidade, 0.0);
        validar();
    }

    public static Set<Ativo> ativoSimplificadosToAtivo(Collection<AtivoSimplificado> ativos) {
        if (Objects.isNull(ativos)) {
            return Collections.emptySet();
        }

        return ativos.stream().map(ativoSimplificado ->
                switch (ativoSimplificado.tipoAtivo()) {
                    case ACAO_NACIONAL -> {
                        yield AcaoNacional.fromSimplificado(ativoSimplificado);
                    }
                    case ACAO_INTERNACIONAL -> {
                        yield AcaoInternacional.fromSimplificado(ativoSimplificado);
                    }
                    case REITs -> AtivoComTicker.fromSimplificado(ativoSimplificado);
                    case FII -> AtivoComTicker.fromSimplificado(ativoSimplificado);
                    case CRYPTO -> AtivoComTicker.fromSimplificado(ativoSimplificado);
                    case RENDA_FIXA -> new RendaFixa(
                            ativoSimplificado.tipoAtivo(),
                            ativoSimplificado.papel(),
                            0,
                            0,
                            ativoSimplificado.nota(),
                            0,
                            ativoSimplificado.quantidade()
                    );
                }).collect(Collectors.toUnmodifiableSet());
    }

    public void validar() throws DominioException {
        if (quantidade < 0) {
            throw new DominioException("Quantidade não pode ser menor que zero");
        }
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }

    public String getLocalAlocado() {
        return localAlocado;
    }

    public double getPercentualRecomendado() {
        return percentualRecomendado;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public Integer getNota() {
        return nota;
    }

    public double getPercentualTotal() {
        return percentualTotal;
    }

    public double getQuantidade() {
        return quantidade;
    }
}
