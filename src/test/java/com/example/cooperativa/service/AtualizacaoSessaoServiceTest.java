package com.example.cooperativa.service;

import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AtualizacaoSessaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private AtualizacaoSessaoService atualizacaoSessaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void deveEncerrarSessoesExpiradasQuandoExistemSessoes() {
        SessaoVotacao sessao1 = SessaoVotacao.builder()
                .id(1L)
                .fim(LocalDateTime.now().minusDays(1))
                .encerrada(false)
                .build();

        SessaoVotacao sessao2 = SessaoVotacao.builder()
                .id(2L)
                .fim(LocalDateTime.now().minusHours(5))
                .encerrada(false)
                .build();

        when(sessaoVotacaoRepository.findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(sessao1, sessao2));

        atualizacaoSessaoService.atualizarSessoesEncerradas();

        ArgumentCaptor<SessaoVotacao> captor = ArgumentCaptor.forClass(SessaoVotacao.class);
        verify(sessaoVotacaoRepository, times(2)).save(captor.capture());

        List<SessaoVotacao> sessoesSalvas = captor.getAllValues();
        assertTrue(sessoesSalvas.get(0).isEncerrada());
        assertTrue(sessoesSalvas.get(1).isEncerrada());

        verify(sessaoVotacaoRepository).findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class));
    }

    @Test
    void naoDeveEncerrarNenhumaSessaoQuandoNaoExistiremSessoesExpiradas() {
        when(sessaoVotacaoRepository.findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        atualizacaoSessaoService.atualizarSessoesEncerradas();

        verify(sessaoVotacaoRepository, never()).save(any(SessaoVotacao.class));

        verify(sessaoVotacaoRepository).findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class));
    }

    @Test
    void deveExecutarAtualizacaoAoIniciarAplicacao() {
        atualizacaoSessaoService.atualizarSessoesEncerradasAoIniciar();

        verify(sessaoVotacaoRepository).findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class));
    }
}