package com.example.cooperativa.service;

import com.example.cooperativa.dto.DetalheVotoDTO;
import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultadoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoVotacaoService resultadoVotacaoService;

    private SessaoVotacao sessaoVotacao;

    @BeforeEach
    void setUp() {
        final Pauta pauta = Pauta.builder().id(1L).nome("Pauta 1").descricao("Descrição da Pauta 1").build();
        sessaoVotacao = SessaoVotacao.builder().id(1L).pauta(pauta).build();

        final Voto voto1 = Voto.builder().id(1L).sessaoVotacao(sessaoVotacao)
                .associado(Associado.builder().id(1L).build()).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto voto2 = Voto.builder().id(2L).sessaoVotacao(sessaoVotacao)
                .associado(Associado.builder().id(2L).build()).votoEscolhido(VotoEscolhido.NAO).build();

        final List<Voto> votos = List.of(voto1, voto2);

        sessaoVotacao.setVotos(votos);
    }

    @Test
    void deveObterResultadoVotacao() {


        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.findBySessaoVotacao(sessaoVotacao)).thenReturn(sessaoVotacao.getVotos());

        final Optional<ResultadoVotacaoDTO> resultado = resultadoVotacaoService.obterResultadoVotacao(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().sessaoVotacaoId());
        assertEquals(1L, resultado.get().pautaId());
        assertEquals(1, resultado.get().votosSim());
        assertEquals(1, resultado.get().votosNao());

        final List<DetalheVotoDTO> detalhesVotos = resultado.get().detalhesVotos();
        assertEquals(2, detalhesVotos.size());
        assertEquals(1L, detalhesVotos.get(0).associadoId());
        assertEquals("SIM", detalhesVotos.get(0).votoEscolhido());
        assertEquals(2L, detalhesVotos.get(1).associadoId());
        assertEquals("NAO", detalhesVotos.get(1).votoEscolhido());

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
        verify(votoRepository, times(1)).findBySessaoVotacao(sessaoVotacao);
    }

    @Test
    void deveLancarSessaoVotacaoNotFoundException() {
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SessaoVotacaoNotFoundException.class, () -> resultadoVotacaoService.obterResultadoVotacao(1L));

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
        verify(votoRepository, times(0)).findBySessaoVotacao(any());
    }

    @Test
    void deveRetornarResultadoVotacaoSemVotos() {
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.findBySessaoVotacao(sessaoVotacao)).thenReturn(List.of());

        final Optional<ResultadoVotacaoDTO> resultado = resultadoVotacaoService.obterResultadoVotacao(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().sessaoVotacaoId());
        assertEquals(1L, resultado.get().pautaId());
        assertEquals(0, resultado.get().votosSim());
        assertEquals(0, resultado.get().votosNao());
        assertEquals(0, resultado.get().detalhesVotos().size());

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
        verify(votoRepository, times(1)).findBySessaoVotacao(sessaoVotacao);
    }

    @Test
    void deveContarCorretamenteOsVotosSimENao() {
        final Voto votoSim1 = Voto.builder().id(1L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(1L).build()).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto votoSim2 = Voto.builder().id(2L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(2L).build()).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto votoNao = Voto.builder().id(3L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(3L).build()).votoEscolhido(VotoEscolhido.NAO).build();
        final List<Voto> votos = List.of(votoSim1, votoSim2, votoNao);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.findBySessaoVotacao(sessaoVotacao)).thenReturn(votos);

        final Optional<ResultadoVotacaoDTO> resultado = resultadoVotacaoService.obterResultadoVotacao(1L);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().votosSim());
        assertEquals(1, resultado.get().votosNao());

        final List<DetalheVotoDTO> detalhesVotos = resultado.get().detalhesVotos();
        assertEquals(3, detalhesVotos.size());
        assertEquals(1L, detalhesVotos.get(0).associadoId());
        assertEquals("SIM", detalhesVotos.get(0).votoEscolhido());
        assertEquals(2L, detalhesVotos.get(1).associadoId());
        assertEquals("SIM", detalhesVotos.get(1).votoEscolhido());
        assertEquals(3L, detalhesVotos.get(2).associadoId());
        assertEquals("NAO", detalhesVotos.get(2).votoEscolhido());

        verify(sessaoVotacaoRepository, times(1)).findById(1L);
        verify(votoRepository, times(1)).findBySessaoVotacao(sessaoVotacao);
    }
}