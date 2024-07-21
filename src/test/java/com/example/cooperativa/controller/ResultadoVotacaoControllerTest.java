package com.example.cooperativa.controller;

import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.service.ResultadoVotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ResultadoVotacaoControllerTest {

    @Mock
    private ResultadoVotacaoService resultadoVotacaoService;

    @InjectMocks
    private ResultadoVotacaoController resultadoVotacaoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resultadoVotacaoController).build();
    }

    @Test
    void deveObterResultadoVotacao() throws Exception {
        final ResultadoVotacaoDTO resultadoVotacaoDTO = ResultadoVotacaoDTO.builder()
                .sessaoVotacaoId(1L)
                .pautaId(1L)
                .votosSim(10)
                .votosNao(5)
                .build();

        when(resultadoVotacaoService.obterResultadoVotacao(1L)).thenReturn(Optional.of(resultadoVotacaoDTO));

        mockMvc.perform(get("/api/resultados/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessaoVotacaoId", is(1)))
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.votosSim", is(10)))
                .andExpect(jsonPath("$.votosNao", is(5)));

        verify(resultadoVotacaoService, times(1)).obterResultadoVotacao(1L);
    }

    @Test
    void deveRetornarNotFoundQuandoVotacaoNaoExistir() throws Exception {
        when(resultadoVotacaoService.obterResultadoVotacao(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/resultados/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(resultadoVotacaoService, times(1)).obterResultadoVotacao(1L);
    }
}