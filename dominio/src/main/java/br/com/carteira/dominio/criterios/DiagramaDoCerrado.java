package br.com.carteira.dominio.criterios;

import java.util.List;

public class DiagramaDoCerrado {
    private List<Criterio> criterios;

    public DiagramaDoCerrado() {
        listPadraoDeCriterios();
    }

    private void listPadraoDeCriterios() {
        criterios = List.of(
                new Criterio("ROE", "ROE historicamente maior que 5%? (Considere anos anteriores)."),
                new Criterio("DÍVIDA LÍQUIDA - LUCRO LÍQUIDO", "A dívida líquida da empresa é menor que o lucro líquido dos últimos 12 meses?"),
                new Criterio("LUCRO OPERACIONAL", "A empresa teve lucro operacional no último exercício?"),
                new Criterio("TEMPO DE MERCADO", "Tem mais de 30 anos de mercado? (Fundação)"),
                new Criterio("CAGR", "Tem um crescimento de receitas (Ou lucro) superior a 5% nos últimos 5 anos?"),
                new Criterio("DIVIDENDOS", "A empresa tem um histórico de pagamento de dividendos?"),
                new Criterio("TECNOLOGIA E PESQUISA", "A empresa investe amplamente em pesquisa e inovação? Setor Obsoleto = SEMPRE NÃO"),
                new Criterio("'P/VP", "A empresa está negociada com um P/VP abaixo de 5?"),
                new Criterio("VANTAGENS COMPETITIVAS", "É líder nacional ou mundial no setor em que atua? (Só considera se for LÍDER, primeira colocada)"),
                new Criterio("PERENIDADE", "O setor em que a empresa atua tem mais de 100 anos?"),
                new Criterio("TAMANHO", "A empresa é uma BLUE CHIP?"),
                new Criterio("INDEPENDÊNCIA", "É livre de controle ESTATAL ou concentração em cliente único?"),
                new Criterio("P/L", "Preço/Lucro da empresa está abaixo de 30?"),
                new Criterio("GOVERNANÇA", "A empresa tem uma boa gestão? Histórico de corrupção = SEMPRE NÃO"),
                new Criterio("DÍVIDA RUIM", "Div. Líquida for negatica ou DL/P/L for menor que 50%, sempre SIM.")
        );
    }

    public List<Criterio> getCriterios() {
        return criterios;
    }
}
