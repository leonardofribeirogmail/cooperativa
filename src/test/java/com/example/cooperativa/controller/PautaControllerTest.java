package com.example.cooperativa.controller;

import com.example.cooperativa.dto.CriarPautaDTO;
import com.example.cooperativa.dto.PautaDTO;
import com.example.cooperativa.exception.GlobalExceptionHandler;
import com.example.cooperativa.service.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private PautaController pautaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pautaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveCriarUmaPauta() throws Exception {
        final CriarPautaDTO criarPautaDTO = CriarPautaDTO.builder()
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();

        final PautaDTO pautaDTO = PautaDTO.builder()
                        .id(1L).nome(criarPautaDTO.nome())
                        .descricao(criarPautaDTO.descricao()).build();

        when(pautaService.criarPauta(any(CriarPautaDTO.class))).thenReturn(pautaDTO);

        mockMvc.perform(post("/api/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Pauta 1\", \"descricao\": \"Descrição Pauta 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Pauta 1")))
                .andExpect(jsonPath("$.descricao", is("Descrição Pauta 1")));

        verify(pautaService, times(1)).criarPauta(any(CriarPautaDTO.class));
    }

    @Test
    void deveListarPautas() throws Exception {
        final List<PautaDTO> pautas = Arrays.asList(
                PautaDTO.builder().id(1L).nome("Pauta 1").descricao("Descrição Pauta 1").build(),
                PautaDTO.builder().id(2L).nome("Pauta 2").descricao("Descrição Pauta 2").build()
        );

        when(pautaService.listarPautas()).thenReturn(pautas);

        mockMvc.perform(get("/api/pautas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Pauta 1")))
                .andExpect(jsonPath("$[1].nome", is("Pauta 2")));

        verify(pautaService, times(1)).listarPautas();
    }

    @Test
    void deveObterPautaPorId() throws Exception {
        final PautaDTO pautaDTO = PautaDTO.builder()
                .id(1L)
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();

        when(pautaService.obterPautaPorId(1L)).thenReturn(Optional.of(pautaDTO));

        mockMvc.perform(get("/api/pautas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Pauta 1")))
                .andExpect(jsonPath("$.descricao", is("Descrição Pauta 1")));

        verify(pautaService, times(1)).obterPautaPorId(1L);
    }

    @Test
    void naoDeveObterPautaPorId() throws Exception {
        when(pautaService.obterPautaPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pautas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(pautaService, times(1)).obterPautaPorId(1L);
    }
}