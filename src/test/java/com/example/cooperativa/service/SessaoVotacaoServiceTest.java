package com.example.cooperativa.service;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
import com.example.cooperativa.exception.PautaNotFoundException;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.PautaRepository;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.util.CacheAlias;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class SessaoVotacaoServiceTest {

    @MockBean
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @MockBean
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoService sessaoVotacaoService;

    @Autowired
    private CacheManager cacheManager;

    private final long schedulerUpdateRate = 60000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessaoVotacaoService, "schedulerUpdateTime", schedulerUpdateRate);
        final Cache cache = cacheManager.getCache(CacheAlias.SESSOES);
        Optional.ofNullable(cache).ifPresent(Cache::clear);
    }

    @Test
    void deveCriarSessao() {
        final Pauta pauta = Pauta.builder()
                .id(1L)
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();

        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plus(schedulerUpdateRate, ChronoUnit.MILLIS);
        final SessaoVotacao sessaoVotacao = criarSessao(1L, inicio, fim, false);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoVotacao);

        final SessaoVotacaoDTO resultado = sessaoVotacaoService.criarSessao(1L);

        assertNotNull(resultado);
        assertEquals(sessaoVotacao.getId(), resultado.id());
        assertEquals(sessaoVotacao.getPauta().getId(), resultado.pautaId());
        assertEquals(sessaoVotacao.isEncerrada(), resultado.encerrada());
        assertEquals(sessaoVotacao.getInicio(), resultado.inicio());
        assertEquals(sessaoVotacao.getFim(), resultado.fim());

        verify(pautaRepository, times(1)).findById(1L);
        verify(sessaoVotacaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    void deveLancarPautaNotFoundExceptionAoCriarSessao() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PautaNotFoundException.class, () -> sessaoVotacaoService.criarSessao(1L));

        verify(pautaRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(sessaoVotacaoRepository);
    }

    @Test
    void deveListarSessoes() {
        final SessaoVotacao sessao1 = criarSessao(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), false);
        final SessaoVotacao sessao2 = criarSessao(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), true);

        when(sessaoVotacaoRepository.findAll()).thenReturn(Arrays.asList(sessao1, sessao2));

        final List<SessaoVotacaoDTO> sessoes = sessaoVotacaoService.listarSessoes();

        assertEquals(2, sessoes.size());
        assertSessaoVotacaoDTO(sessao1, sessoes.get(0));
        assertSessaoVotacaoDTO(sessao2, sessoes.get(1));

        verify(sessaoVotacaoRepository, times(1)).findAll();
    }

    @Test
    void deveListarSessoesComCache() {
        final SessaoVotacao sessao1 = criarSessao(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), false);
        final SessaoVotacao sessao2 = criarSessao(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), true);

        when(sessaoVotacaoRepository.findAll()).thenReturn(Arrays.asList(sessao1, sessao2));

        // Primeira chamada, deve buscar do repositório
        final List<SessaoVotacaoDTO> sessoes1 = sessaoVotacaoService.listarSessoes();
        assertEquals(2, sessoes1.size());
        verify(sessaoVotacaoRepository, times(1)).findAll();

        // Segunda chamada, deve buscar do cache
        final List<SessaoVotacaoDTO> sessoes2 = sessaoVotacaoService.listarSessoes();
        assertEquals(2, sessoes2.size());
        verify(sessaoVotacaoRepository, times(1)).findAll();
    }

    @Test
    void deveObterSessaoPorId() {
        final SessaoVotacao sessao = criarSessao(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), false);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));

        final SessaoVotacaoDTO result = sessaoVotacaoService.obterSessaoPorId(1L);

        assertNotNull(result);
        assertSessaoVotacaoDTO(sessao, result);

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
    }

    @Test
    void deveObterSessaoPorIdComCache() {
        final SessaoVotacao sessao = criarSessao(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now(), false);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessao));

        // Primeira chamada, deve buscar do repositório
        final SessaoVotacaoDTO result1 = sessaoVotacaoService.obterSessaoPorId(1L);
        assertNotNull(result1);
        assertSessaoVotacaoDTO(sessao, result1);
        verify(sessaoVotacaoRepository, times(1)).findById(1L);

        // Segunda chamada, deve buscar do cache
        final SessaoVotacaoDTO result2 = sessaoVotacaoService.obterSessaoPorId(1L);
        assertNotNull(result2);
        assertSessaoVotacaoDTO(sessao, result2);
        verify(sessaoVotacaoRepository, times(1)).findById(1L);
    }

    @Test
    void deveLancarSessaoNaoEncontradaException() {
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SessaoVotacaoNotFoundException.class, () -> sessaoVotacaoService.obterSessaoPorId(1L));

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
    }

    @Test
    void deveEncerrarSessaoSeExpirada() {
        final SessaoVotacao sessaoVotacao = criarSessao(1L, LocalDateTime.now().minusMinutes(2), LocalDateTime.now().minusMinutes(1), false);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));

        sessaoVotacaoService.encerrarSessaoSeExpirada(1L);

        assertTrue(sessaoVotacao.isEncerrada());
        verify(sessaoVotacaoRepository).findById(1L);
        verify(sessaoVotacaoRepository).save(sessaoVotacao);
    }

    @Test
    void naoDeveEncerrarSessaoSeNaoExpirada() {
        final SessaoVotacao sessaoVotacao = criarSessao(1L, LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1), false);
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));

        sessaoVotacaoService.encerrarSessaoSeExpirada(1L);

        assertFalse(sessaoVotacao.isEncerrada());
        verify(sessaoVotacaoRepository).findById(1L);
        verify(sessaoVotacaoRepository, never()).save(any(SessaoVotacao.class));
    }

    @Test
    void deveEvictarCacheAoCriarSessao() {
        final Pauta pauta = Pauta.builder()
                .id(1L)
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();

        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plus(schedulerUpdateRate, ChronoUnit.MILLIS);
        final SessaoVotacao sessaoVotacao = criarSessao(1L, inicio, fim, false);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoVotacao);

        sessaoVotacaoService.criarSessao(1L);

        Cache cache = cacheManager.getCache(CacheAlias.SESSOES);
        assertNotNull(cache);
        assertNull(cache.get(1L));
    }

    private SessaoVotacao criarSessao(final Long id,
                                      final LocalDateTime inicio,
                                      final LocalDateTime fim,
                                      final boolean encerrada) {
        final Pauta pauta = Pauta.builder()
                .id(1L)
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();
        return SessaoVotacao.builder()
                .id(id)
                .inicio(inicio)
                .fim(fim)
                .encerrada(encerrada)
                .pauta(pauta)
                .build();
    }

    private void assertSessaoVotacaoDTO(final SessaoVotacao expected, final SessaoVotacaoDTO actual) {
        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getPauta().getId(), actual.pautaId());
        assertEquals(expected.isEncerrada(), actual.encerrada());
        assertEquals(expected.getInicio(), actual.inicio());
        assertEquals(expected.getFim(), actual.fim());
    }
}