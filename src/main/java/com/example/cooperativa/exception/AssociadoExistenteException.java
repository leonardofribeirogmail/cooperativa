package com.example.cooperativa.exception;

public class AssociadoExistenteException extends RuntimeException {
    public AssociadoExistenteException(final String mensagem) {
        super(mensagem);
    }
}
