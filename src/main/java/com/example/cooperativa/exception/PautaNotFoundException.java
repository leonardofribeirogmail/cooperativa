package com.example.cooperativa.exception;

public class PautaNotFoundException extends RuntimeException {
    public PautaNotFoundException(final String mensagem) {
        super(mensagem);
    }
}