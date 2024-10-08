package com.example.cooperativa.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CPFUtilTest {

    private CPFUtil cpfUtil;

    @BeforeEach
    public void setUp() {
        cpfUtil = new CPFUtil();
    }

    @Test
    void deveRetornarTrueQuandoCpfForValido() {
        final String validCpf = "12345678909";
        assertTrue(cpfUtil.isCpfValido(validCpf));
    }

    @Test
    void deveRetornarFalseQuandoCpfForInvalido() {
        final String invalidCpf = "12345678900";
        assertFalse(cpfUtil.isCpfValido(invalidCpf));
    }
}