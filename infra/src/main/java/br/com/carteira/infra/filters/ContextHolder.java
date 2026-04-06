package br.com.carteira.infra.filters;

import org.springframework.stereotype.Component;

@Component
public class ContextHolder {
    private String userName;
    private String email;
    private String identificacao;

    public String getUserName() {
        return userName;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
