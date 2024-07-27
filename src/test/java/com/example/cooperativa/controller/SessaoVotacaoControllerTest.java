package com.example.cooperativa.controller;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
import com.example.cooperativa.service.SessaoVotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoControllerTest {

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @InjectMocks
    private SessaoVotacaoController sessaoVotacaoController;

    private MockMvc mockMvc;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessaoVotacaoController).build();
    }

    @Test
    void deveCriarUmaSessao() throws Exception {
        final Long pautaId = 1L;

        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plusMinutes(1);

        final SessaoVotacaoDTO sessaoVotacaoDTO = SessaoVotacaoDTO.builder()
                .id(1L)
                .pautaId(pautaId)
                .inicio(inicio)
                .fim(fim)
                .build();

        when(sessaoVotacaoService.criarSessao(any(Long.class)))
                .thenReturn(sessaoVotacaoDTO);

        mockMvc.perform(post("/api/sessoes")
                        .param("pautaId", pautaId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.pautaId", equalTo(pautaId.intValue())))
                .andExpect(jsonPath("$.inicio", equalTo(inicio.format(FORMATTER))))
                .andExpect(jsonPath("$.fim", equalTo(fim.format(FORMATTER))));

        verify(sessaoVotacaoService, times(1)).criarSessao(any(Long.class));
    }

    @Test
    void deveListarSessoes() throws Exception {
        final List<SessaoVotacaoDTO> sessoes = getSessaoVotacaoDTO();

        when(sessaoVotacaoService.listarSessoes()).thenReturn(sessoes);

        mockMvc.perform(get("/api/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[1].id", equalTo(2)));

        verify(sessaoVotacaoService, times(1)).listarSessoes();
    }

    @Test
    void deveObterSessaoPorId() throws Exception {
        final Long sessaoId = 1L;

        final LocalDateTime inicio = LocalDateTime.now().withNano(0);
        final LocalDateTime fim = inicio.plusMinutes(1).withNano(0);

        final SessaoVotacaoDTO sessaoVotacaoDTO = SessaoVotacaoDTO.builder()
                .id(sessaoId)
                .pautaId(1L)
                .inicio(inicio)
                .fim(fim)
                .build();

        when(sessaoVotacaoService.obterSessaoPorId(sessaoId)).thenReturn(sessaoVotacaoDTO);

        mockMvc.perform(get("/api/sessoes/{id}", sessaoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(sessaoId.intValue())))
                .andExpect(jsonPath("$.pautaId", equalTo(1)))
                .andExpect(jsonPath("$.inicio", equalTo(inicio.format(FORMATTER))))
                .andExpect(jsonPath("$.fim", equalTo(fim.format(FORMATTER))));

        verify(sessaoVotacaoService, times(1)).obterSessaoPorId(sessaoId);
    }

    @Test
    void naoDeveObterSessaoPorId() throws Exception {
        final Long sessaoId = 1L;

        when(sessaoVotacaoService.obterSessaoPorId(sessaoId)).thenReturn(null);

        mockMvc.perform(get("/api/sessoes/{id}", sessaoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(sessaoVotacaoService, times(1)).obterSessaoPorId(sessaoId);
    }

    private List<SessaoVotacaoDTO> getSessaoVotacaoDTO() {
        return Arrays.asList(
                SessaoVotacaoDTO.builder().id(1L).pautaId(1L).inicio(LocalDateTime.now()).fim(LocalDateTime.now().plusHours(1)).build(),
                SessaoVotacaoDTO.builder().id(2L).pautaId(2L).inicio(LocalDateTime.now()).fim(LocalDateTime.now().plusHours(1)).build()
        );
    }
}