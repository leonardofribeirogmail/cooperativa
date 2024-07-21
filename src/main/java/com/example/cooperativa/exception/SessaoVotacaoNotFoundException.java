package com.example.cooperativa.exception;

public class SessaoVotacaoNotFoundException extends RuntimeException {
    public SessaoVotacaoNotFoundException(String mensagem) {
        super(mensagem);
    }
}