package com.example.cooperativa.service;

import com.example.cooperativa.dto.CpfValidationResponseDTO;
import com.example.cooperativa.exception.CpfValidationException;
import com.example.cooperativa.exception.InvalidCPFException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CPFValidationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CPFValidationService cpfValidationService;

    @BeforeEach
    void setUp() {
        cpfValidationService = new CPFValidationService(restTemplate, "http://localhost/{cpf}");
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        final String invalidCpf = "12345678909";
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO("UNABLE_TO_VOTE");

        when(restTemplate.getForObject(anyString(), eq(CpfValidationResponseDTO.class))).thenReturn(response);

        assertThrows(InvalidCPFException.class, () -> cpfValidationService.validarCpfNoServicoExterno(invalidCpf));
    }

    @Test
    void deveLancarExcecaoQuandoTimeout() {
        final String cpf = "12345678909";

        doThrow(new ResourceAccessException("Timeout"))
                .when(restTemplate)
                .getForObject(anyString(), eq(CpfValidationResponseDTO.class));

        assertThrows(CpfValidationException.class, () -> cpfValidationService.validarCpfNoServicoExterno(cpf));
    }

    @Test
    void deveLancarExcecaoGenerica() {
        final String cpf = "12345678909";

        doThrow(new RuntimeException("Erro geral"))
                .when(restTemplate)
                .getForObject(anyString(), eq(CpfValidationResponseDTO.class));

        assertThrows(CpfValidationException.class, () -> cpfValidationService.validarCpfNoServicoExterno(cpf));
    }

    @Test
    void deveValidarCpfComSucesso() {
        final String validCpf = "12345678900";
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO("ABLE_TO_VOTE");

        when(restTemplate.getForObject(anyString(), eq(CpfValidationResponseDTO.class))).thenReturn(response);

        assertDoesNotThrow(() -> cpfValidationService.validarCpfNoServicoExterno(validCpf));
    }
}