package com.example.cooperativa.controller;

import com.example.cooperativa.dto.VotoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VotoControllerTest {

    @Mock
    private VotoService votoService;

    @InjectMocks
    private VotoController votoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(votoController).build();
    }

    @Test
    void deveRegistrarUmVoto() throws Exception {
        final VotoDTO votoDTO = VotoDTO.builder()
                .id(1L)
                .sessaoVotacaoId(1L)
                .associadoId(1L)
                .votoEscolhido(VotoEscolhido.SIM)
                .build();

        when(votoService.registrarVoto(any(Long.class), any(Long.class), any(VotoEscolhido.class)))
                .thenReturn(votoDTO);

        mockMvc.perform(post("/api/votos")
                        .param("sessaoVotacaoId", "1")
                        .param("associadoId", "1")
                        .param("votoEscolhido", "SIM"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.sessaoVotacaoId", is(1)))
                .andExpect(jsonPath("$.associadoId", is(1)))
                .andExpect(jsonPath("$.votoEscolhido", is("SIM")));

        verify(votoService, times(1))
                .registrarVoto(any(Long.class), any(Long.class), any(VotoEscolhido.class));
    }
}