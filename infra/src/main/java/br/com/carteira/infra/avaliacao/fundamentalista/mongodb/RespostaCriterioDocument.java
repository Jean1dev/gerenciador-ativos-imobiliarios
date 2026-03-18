package br.com.carteira.infra.avaliacao.fundamentalista.mongodb;

public class RespostaCriterioDocument {
    private String pergunta;
    private boolean resposta;
    private String justificativa;

    public RespostaCriterioDocument() {
    }

    public RespostaCriterioDocument(String pergunta, boolean resposta, String justificativa) {
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.justificativa = justificativa;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public boolean isResposta() {
        return resposta;
    }

    public void setResposta(boolean resposta) {
        this.resposta = resposta;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
