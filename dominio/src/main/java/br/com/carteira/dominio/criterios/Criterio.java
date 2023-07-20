package br.com.carteira.dominio.criterios;

public class Criterio {
    private String criterio;
    private String pergunta;
    private Boolean simOuNao = false;

    public Criterio(String criterio, String pergunta) {
        this.criterio = criterio;
        this.pergunta = pergunta;
    }

    public Criterio() {
    }

    public void setSimOuNao(Boolean simOuNao) {
        this.simOuNao = simOuNao;
    }

    public String getCriterio() {
        return criterio;
    }

    public String getPergunta() {
        return pergunta;
    }

    public Boolean getSimOuNao() {
        return simOuNao;
    }
}
