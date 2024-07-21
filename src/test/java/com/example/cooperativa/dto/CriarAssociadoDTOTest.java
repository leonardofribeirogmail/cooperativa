package com.example.cooperativa.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CriarAssociadoDTOTest {

    @Test
    void deveLimparEspacamentoECaracteresEspeciais() {
        final CriarAssociadoDTO criarAssociadoDTO = new CriarAssociadoDTO("794.519.230-04    ");
        assertEquals("79451923004", criarAssociadoDTO.cpf());
    }
}