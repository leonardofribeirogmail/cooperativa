package com.example.cooperativa.service;

import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EncerramentoSessaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private EncerramentoSessaoService encerramentoSessaoService;

    private List<SessaoVotacao> sessoesExpiradas;

    @BeforeEach
    void setUp() {
        final SessaoVotacao sessao1 = SessaoVotacao.builder()
                .id(1L)
                .fim(LocalDateTime.now().minusMinutes(10))
                .encerrada(false)
                .build();

        final SessaoVotacao sessao2 = SessaoVotacao.builder()
                .id(2L)
                .fim(LocalDateTime.now().minusMinutes(5))
                .encerrada(false)
                .build();

        sessoesExpiradas = Arrays.asList(sessao1, sessao2);

        when(sessaoVotacaoRepository.findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class)))
                .thenReturn(sessoesExpiradas);
    }

    @Test
    void deveEncerrarSessoesExpiradas() {
        encerramentoSessaoService.encerrarSessoesExpiradas();

        for (SessaoVotacao sessao : sessoesExpiradas) {
            verify(sessaoVotacaoRepository, times(1)).save(sessao);
        }

        verify(sessaoVotacaoRepository, times(1)).findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class));
    }
}