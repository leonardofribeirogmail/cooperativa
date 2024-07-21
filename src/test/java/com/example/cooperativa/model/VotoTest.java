package com.example.cooperativa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class VotoTest {

    @Test
    void deveCriarVotoComConstrutorSemArgumentos() {
        Voto voto = new Voto();
        assertNotNull(voto);
    }
}