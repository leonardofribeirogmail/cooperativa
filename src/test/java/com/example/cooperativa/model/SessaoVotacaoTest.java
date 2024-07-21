package com.example.cooperativa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SessaoVotacaoTest {

    @Test
    void deveCriarSessaoVotacaoComConstrutorSemArgumentos() {
        SessaoVotacao sessaoVotacao = new SessaoVotacao();
        assertNotNull(sessaoVotacao);
    }
}