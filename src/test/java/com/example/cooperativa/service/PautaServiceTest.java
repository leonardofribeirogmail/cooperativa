package com.example.cooperativa.service;

import com.example.cooperativa.dto.CriarPautaDTO;
import com.example.cooperativa.dto.PautaDTO;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

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

    private Pauta criarPauta(final Long id,
                             final String nome,
                             final String descricao) {
        return Pauta.builder().id(id).nome(nome).descricao(descricao).build();
    }
}