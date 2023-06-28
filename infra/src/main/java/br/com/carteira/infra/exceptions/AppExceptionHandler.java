package br.com.carteira.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.NoSuchElementException;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    public static ResponseEntity<ErrorResponse> defaultHanlder(Exception ex, WebRequest request) {
        var errorResponse = new ErrorResponse(
                ex.getLocalizedMessage(),
                Collections.emptyList(),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.class)
    public final ResponseEntity<ErrorResponse> applicationExceptionHandler(ApplicationException ex, WebRequest req) {
        return defaultHanlder(ex, req);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public final ResponseEntity<ErrorResponse> noSuchElement(NoSuchElementException ex, WebRequest req) {
        var errorResponse = new ErrorResponse(
                "Não encontrado uma entidade para esses parametros",
                Collections.emptyList(),
                HttpStatus.BAD_REQUEST.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
