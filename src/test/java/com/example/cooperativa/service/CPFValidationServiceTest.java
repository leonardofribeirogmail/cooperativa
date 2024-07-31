package com.example.cooperativa.service;

import com.example.cooperativa.dto.CpfValidationResponseDTO;
import com.example.cooperativa.exception.CpfValidationException;
import com.example.cooperativa.exception.InvalidCPFException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.example.cooperativa.util.CacheAlias.CPF_VALIDATION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CPFValidationServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CPFValidationService cpfValidationService;

    @Autowired
    private CacheManager cacheManager;

    private static final String VALID_CPF = "12345678900";
    private static final String INVALID_CPF = "12345678909";

    @BeforeEach
    void setUp() {
        final Cache cache = cacheManager.getCache(CPF_VALIDATION);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO("UNABLE_TO_VOTE");

        when(restTemplate.getForObject(anyString(), eq(CpfValidationResponseDTO.class))).thenReturn(response);

        assertThrows(InvalidCPFException.class, () -> cpfValidationService.validarCpfNoServicoExterno(INVALID_CPF));
    }

    @Test
    void deveLancarExcecaoQuandoTimeout() {
        doThrow(new ResourceAccessException("Timeout"))
                .when(restTemplate)
                .getForObject(anyString(), eq(CpfValidationResponseDTO.class));

        assertThrows(CpfValidationException.class, () -> cpfValidationService.validarCpfNoServicoExterno(INVALID_CPF));
    }

    @Test
    void deveLancarExcecaoGenerica() {
        doThrow(new RuntimeException("Erro geral"))
                .when(restTemplate)
                .getForObject(anyString(), eq(CpfValidationResponseDTO.class));

        assertThrows(CpfValidationException.class, () -> cpfValidationService.validarCpfNoServicoExterno(INVALID_CPF));
    }

    @Test
    void deveValidarCpfComSucesso() {
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO("ABLE_TO_VOTE");

        when(restTemplate.getForObject(anyString(), eq(CpfValidationResponseDTO.class))).thenReturn(response);

        assertDoesNotThrow(() -> cpfValidationService.validarCpfNoServicoExterno(VALID_CPF));
    }

    @Test
    void deveUtilizarCacheParaValidarCpf() {
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO("ABLE_TO_VOTE");

        when(restTemplate.getForObject(anyString(), eq(CpfValidationResponseDTO.class))).thenReturn(response);

        assertDoesNotThrow(() -> cpfValidationService.validarCpfNoServicoExterno(VALID_CPF));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CpfValidationResponseDTO.class));

        assertDoesNotThrow(() -> cpfValidationService.validarCpfNoServicoExterno(VALID_CPF));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(CpfValidationResponseDTO.class));
    }
}