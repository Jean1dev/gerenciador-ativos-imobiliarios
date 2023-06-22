package br.com.carteira.dominio.criterios;

import java.util.List;

public class AvaliacaoFundosImobiliarios {

    private List<Criterio> criterios;

    public AvaliacaoFundosImobiliarios() {
        listaPadrao();
    }

    private void listaPadrao() {
        criterios = List.of(
                new Criterio("", "As propriedades são novas e não consomem manutenção excessiva?"),
                new Criterio("", "Os imóveis desse Fundo Imobiliário estão localizados em regiões nobres?"),
                new Criterio("", "O fundo imobiliário está negociado abaixo do P/VP 1? (Acima de 1,5, eu descarto o investimento em qualquer hipótese)"),
                new Criterio("", "Distribui dividendos a mais de 4 anos consistentemente?"),
                new Criterio("", "O Yield está dentro ou acima da média para fundos imobiliários do mesmo tipo?"),
                new Criterio("", "É dependente de um único inquilino ou imóvel? (Em caso afirmativo, costumo descartar, mas a opção é sua)")
        );
    }
}
