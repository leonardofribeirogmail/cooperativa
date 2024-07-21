package com.example.cooperativa.exception;

public class VotoDuplicadoException extends RuntimeException {
    public VotoDuplicadoException(final String mensagem) {
        super(mensagem);
    }
}
