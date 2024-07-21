package com.example.cooperativa.controller;

import com.example.cooperativa.dto.AssociadoResponseDTO;
import com.example.cooperativa.dto.CriarAssociadoDTO;
import com.example.cooperativa.exception.GlobalExceptionHandler;
import com.example.cooperativa.service.AssociadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AssociadoControllerTest {

    @Mock
    private AssociadoService associadoService;

    @InjectMocks
    private AssociadoController associadoController;

    private MockMvc mockMvc;

    @Test
    void deveCriarAssociadoComSucesso() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(associadoController).build();

        final CriarAssociadoDTO criarAssociadoDTO = new CriarAssociadoDTO("12345678901");
        final AssociadoResponseDTO responseDTO = AssociadoResponseDTO.builder()
                .id(1L)
                .cpf("12345678901")
                .build();

        when(associadoService.criarAssociado(any(CriarAssociadoDTO.class))).thenReturn(responseDTO);

        final ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(criarAssociadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cpf").value("12345678901"));

        verify(associadoService, times(1)).criarAssociado(any(CriarAssociadoDTO.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(associadoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        doThrow(new IllegalArgumentException("CPF inválido: 12345678901"))
                .when(associadoService).criarAssociado(any(CriarAssociadoDTO.class));

        mockMvc.perform(post("/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"12345678901\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result ->
                        assertEquals("CPF inválido: 12345678901",
                                Objects.requireNonNull(result.getResolvedException()).getMessage())
                );

        verify(associadoService, times(1)).criarAssociado(any(CriarAssociadoDTO.class));
    }

    @Test
    void deveListarTodosOsAssociados() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(associadoController).build();

        AssociadoResponseDTO responseDTO = AssociadoResponseDTO.builder()
                .id(1L)
                .cpf("12345678901")
                .build();

        when(associadoService.listarAssociados()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/associados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cpf").value("12345678901"));

        verify(associadoService, times(1)).listarAssociados();
    }
}