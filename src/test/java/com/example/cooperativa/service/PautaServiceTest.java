package com.example.cooperativa.service;

import com.example.cooperativa.dto.CriarPautaDTO;
import com.example.cooperativa.dto.PautaDTO;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.repository.PautaRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class PautaServiceTest {

    @MockBean
    private PautaRepository pautaRepository;

    @Autowired
    private PautaService pautaService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        final Cache cache = cacheManager.getCache(CacheAlias.PAUTAS);
        Optional.ofNullable(cache).ifPresent(Cache::clear);
    }

    @Test
    void deveCriarUmaPauta() {
        final CriarPautaDTO criarPautaDTO = CriarPautaDTO.builder()
                .nome("Pauta 1")
                .descricao("Descrição Pauta 1")
                .build();

        final Pauta pauta = criarPauta(1L, criarPautaDTO.nome(), criarPautaDTO.descricao());

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        final PautaDTO result = pautaService.criarPauta(criarPautaDTO);

        assertEquals(criarPautaDTO.nome(), result.nome());
        assertEquals(criarPautaDTO.descricao(), result.descricao());
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    void deveObterPautaPorId() {
        final Pauta pauta = criarPauta(1L, "Pauta 1", "Descrição Pauta 1");

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        final Optional<PautaDTO> result = pautaService.obterPautaPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(pauta.getNome(), result.get().nome());
        assertEquals(pauta.getDescricao(), result.get().descricao());
        verify(pautaRepository, times(1)).findById(1L);
    }

    @Test
    void deveObterPautaPorIdComCache() {
        final Pauta pauta = criarPauta(1L, "Pauta 1", "Descrição Pauta 1");

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        // Primeira chamada, deve buscar do repositório
        final Optional<PautaDTO> result1 = pautaService.obterPautaPorId(1L);
        assertTrue(result1.isPresent());
        assertEquals(pauta.getNome(), result1.get().nome());
        assertEquals(pauta.getDescricao(), result1.get().descricao());
        verify(pautaRepository, times(1)).findById(1L);

        // Segunda chamada, deve buscar do cache
        final Optional<PautaDTO> result2 = pautaService.obterPautaPorId(1L);
        assertTrue(result2.isPresent());
        assertEquals(pauta.getNome(), result2.get().nome());
        assertEquals(pauta.getDescricao(), result2.get().descricao());
        verify(pautaRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarFalsoNaBuscaDeUmaPautaPorId() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        final Optional<PautaDTO> result = pautaService.obterPautaPorId(1L);

        assertFalse(result.isPresent());
        verify(pautaRepository, times(1)).findById(1L);
    }

    @Test
    void deveListarPautas() {
        final Pauta pauta1 = criarPauta(1L, "Pauta 1", "Descrição Pauta 1");
        final Pauta pauta2 = criarPauta(2L, "Pauta 2", "Descrição Pauta 2");

        when(pautaRepository.findAll()).thenReturn(Arrays.asList(pauta1, pauta2));

        final List<PautaDTO> pautas = pautaService.listarPautas();

        assertEquals(2, pautas.size());
        assertEquals("Pauta 1", pautas.get(0).nome());
        assertEquals("Descrição Pauta 1", pautas.get(0).descricao());
        assertEquals("Pauta 2", pautas.get(1).nome());
        assertEquals("Descrição Pauta 2", pautas.get(1).descricao());

        verify(pautaRepository, times(1)).findAll();
    }

    @Test
    void deveListarPautasComCache() {
        final Pauta pauta1 = criarPauta(1L, "Pauta 1", "Descrição Pauta 1");
        final Pauta pauta2 = criarPauta(2L, "Pauta 2", "Descrição Pauta 2");

        when(pautaRepository.findAll()).thenReturn(Arrays.asList(pauta1, pauta2));

        // Primeira chamada, deve buscar do repositório
        final List<PautaDTO> pautas1 = pautaService.listarPautas();
        assertEquals(2, pautas1.size());
        verify(pautaRepository, times(1)).findAll();

        // Segunda chamada, deve buscar do cache
        final List<PautaDTO> pautas2 = pautaService.listarPautas();
        assertEquals(2, pautas2.size());
        verify(pautaRepository, times(1)).findAll();
    }

    private Pauta criarPauta(final Long id,
                             final String nome,
                             final String descricao) {
        return Pauta.builder().id(id).nome(nome).descricao(descricao).build();
    }
}