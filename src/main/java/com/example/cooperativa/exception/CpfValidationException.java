package com.example.cooperativa.exception;

public class CpfValidationException extends RuntimeException {
    public CpfValidationException(final String mensagem) {
        super(mensagem);
    }
    public CpfValidationException(final Throwable throwable,
                                  final String mensagem) {
        super(mensagem, throwable);
    }
}
