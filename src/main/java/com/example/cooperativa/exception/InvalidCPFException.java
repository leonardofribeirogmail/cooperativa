package com.example.cooperativa.exception;

public class InvalidCPFException extends RuntimeException {
    public InvalidCPFException(final String mensagem) {
        super(mensagem);
    }
}
