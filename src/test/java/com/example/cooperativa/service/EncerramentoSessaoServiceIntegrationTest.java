package com.example.cooperativa.service;

import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableScheduling
@ActiveProfiles("test")
@TestPropertySource(properties = "scheduler.updateRate=1000")  // Reduzindo o intervalo para 1 segundo para testes
class EncerramentoSessaoServiceIntegrationTest {

    @MockBean
    private SessaoVotacaoRepository sessaoVotacaoRepository;

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

        final List<SessaoVotacao> sessoesExpiradas = Arrays.asList(sessao1, sessao2);

        when(sessaoVotacaoRepository.findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class)))
                .thenReturn(sessoesExpiradas);
    }

    @Test
    void deveExecutarMetodoAgendado() {
        await().atMost(2, SECONDS).untilAsserted(() -> {
            verify(sessaoVotacaoRepository, atLeastOnce()).findByFimBeforeAndEncerradaFalse(any(LocalDateTime.class));
            verify(sessaoVotacaoRepository, times(2)).save(any(SessaoVotacao.class));
        });
    }
}