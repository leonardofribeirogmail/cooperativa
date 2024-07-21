package com.example.cooperativa.exception;

public class AssociadoNotFoundException extends RuntimeException {
    public AssociadoNotFoundException(final String mensagem) {
        super(mensagem);
    }
}
