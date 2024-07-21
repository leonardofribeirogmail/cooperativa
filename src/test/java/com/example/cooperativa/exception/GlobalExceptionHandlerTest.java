package com.example.cooperativa.exception;

import com.example.cooperativa.controller.PautaController;
import com.example.cooperativa.service.PautaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

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
    void handlePautaNotFoundException() throws Exception {
        testarExcecao(new PautaNotFoundException("Pauta not found"), status().isNotFound());
    }

    @Test
    void handleSessaoVotacaoNotFoundException() throws Exception {
        testarExcecao(new SessaoVotacaoNotFoundException("Sessão de votação not found"), status().isNotFound());
    }

    @Test
    void handleAssociadoNotFoundException() throws Exception {
        testarExcecao(new AssociadoNotFoundException("Associado not found"), status().isNotFound());
    }

    @Test
    void handleInvalidCPFException() throws Exception {
        testarExcecao(new InvalidCPFException("Invalid CPF"), status().isNotFound());
    }

    @Test
    void handleCpfValidationException() throws Exception {
        testarExcecao(new CpfValidationException("CPF validation failed"), status().isNotFound());
    }

    @Test
    void handleSessaoVotacaoEncerradaException() throws Exception {
        testarExcecao(new SessaoVotacaoEncerradaException("Sessao Votacao encerrada"), status().isBadRequest());
    }

    @Test
    void handleIllegalArgumentException() throws Exception {
        testarExcecao(new IllegalArgumentException("Illegal argument"), status().isBadRequest());
    }

    @Test
    void handleAssociadoExistenteException() throws Exception {
        testarExcecao(new AssociadoExistenteException("Associado inválido"), status().isBadRequest());
    }

    @Test
    void handleVotoDuplicadoException() throws Exception {
        testarExcecao(new VotoDuplicadoException("Associado já votou nesta sessão de votação"), status().isBadRequest());
    }

    @Test
    void handleErroInesperado() throws Exception {
        testarExcecao(new IndexOutOfBoundsException("Ocorreu um erro inesperado. Tente novamente mais tarde."),
                status().isInternalServerError());
    }

    private void testarExcecao(final Exception excecao,
                               final ResultMatcher statusEsperado) throws Exception {
        doThrow(excecao).when(pautaService).obterPautaPorId(anyLong());

        mockMvc.perform(get("/api/pautas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(statusEsperado)
                .andExpect(content().string(excecao.getMessage()));
    }
}