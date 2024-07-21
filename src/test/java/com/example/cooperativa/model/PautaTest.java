package com.example.cooperativa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PautaTest {

    @Test
    void deveCriarPautaComConstrutorSemArgumentos() {
        Pauta pauta = new Pauta();
        assertNotNull(pauta);
    }
}