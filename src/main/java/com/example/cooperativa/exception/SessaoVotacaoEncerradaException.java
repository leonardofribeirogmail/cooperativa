package com.example.cooperativa.exception;

public class SessaoVotacaoEncerradaException extends RuntimeException {
    public SessaoVotacaoEncerradaException(final String mensagem) {
        super(mensagem);
    }
}
