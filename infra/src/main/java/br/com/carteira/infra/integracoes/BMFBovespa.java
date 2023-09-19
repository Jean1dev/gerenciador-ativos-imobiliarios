package br.com.carteira.infra.integracoes;

public interface BMFBovespa {

    CotacaoDto getCotacao(String ticker);

    String getErrorList();
}
