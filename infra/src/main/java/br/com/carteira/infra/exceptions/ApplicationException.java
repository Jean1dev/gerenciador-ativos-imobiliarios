package br.com.carteira.infra.exceptions;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String mensagem) {
        super(mensagem);
    }
}
