package com.example.cooperativa.service;

import com.example.cooperativa.dto.DetalheVotoDTO;
import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.repository.PautaRepository;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.repository.VotoRepository;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.cooperativa.util.CacheAlias.RESULTADO_VOTACAO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class ResultadoVotacaoServiceTest {

    @MockBean
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @MockBean
    private VotoRepository votoRepository;

    @MockBean
    private AssociadoRepository associadoRepository;

    @MockBean
    private PautaRepository pautaRepository;

    @MockBean
    private CPFValidationService cpfValidationService;

    @Autowired
    private ResultadoVotacaoService resultadoVotacaoService;

    @Autowired
    private SessaoVotacaoService sessaoVotacaoService;

    @Autowired
    private VotoService votoService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @Test
    void deveObterResultadoVotacao() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);
        final List<Voto> votos = criarVotos(sessaoVotacao);

        sessaoVotacao.setVotos(votos);

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));

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

        verify(sessaoVotacaoRepository, times(1)).findByIdWithVotos(1L);
    }

    @Test
    void deveLancarSessaoVotacaoNotFoundException() {
        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.empty());

        assertThrows(SessaoVotacaoNotFoundException.class, () -> resultadoVotacaoService.obterResultadoVotacao(1L));

        verify(sessaoVotacaoRepository, times(1)).findByIdWithVotos(1L);
    }

    @Test
    void deveRetornarResultadoVotacaoSemVotos() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);

        sessaoVotacao.setVotos(List.of());

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));

        final Optional<ResultadoVotacaoDTO> resultado = resultadoVotacaoService.obterResultadoVotacao(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().sessaoVotacaoId());
        assertEquals(1L, resultado.get().pautaId());
        assertEquals(0, resultado.get().votosSim());
        assertEquals(0, resultado.get().votosNao());
        assertEquals(0, resultado.get().detalhesVotos().size());

        verify(sessaoVotacaoRepository, times(1)).findByIdWithVotos(1L);
    }

    @Test
    void deveContarCorretamenteOsVotosSimENao() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);
        final List<Voto> votos = criarVotosComVotosSimENao(sessaoVotacao);

        sessaoVotacao.setVotos(votos);

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));

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

        verify(sessaoVotacaoRepository, times(1)).findByIdWithVotos(1L);
    }

    @Test
    void deveUtilizarCacheParaChamadaRepetida() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);
        final List<Voto> votos = criarVotos(sessaoVotacao);

        sessaoVotacao.setVotos(votos);

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));

        resultadoVotacaoService.obterResultadoVotacao(1L);
        resultadoVotacaoService.obterResultadoVotacao(1L);

        verify(sessaoVotacaoRepository, times(1)).findByIdWithVotos(1L);
    }

    @Test
    void deveInvalidarCacheAoRegistrarVoto() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);
        final List<Voto> votos = criarVotos(sessaoVotacao);
        final Associado associado = criarAssociado();

        sessaoVotacao.setVotos(votos);

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(associadoRepository.findById(1L)).thenReturn(Optional.of(associado));
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        resultadoVotacaoService.obterResultadoVotacao(1L);
        final Cache cache = cacheManager.getCache(RESULTADO_VOTACAO);
        assertNotNull(cache);
        assertNotNull(cache.get(1L));

        final Voto novoVoto = Voto.builder().id(3L).sessaoVotacao(sessaoVotacao)
                .associado(associado).votoEscolhido(VotoEscolhido.SIM).build();

        when(votoRepository.save(any(Voto.class))).thenReturn(novoVoto);
        when(sessaoVotacaoRepository.findById(sessaoVotacao.getId())).thenReturn(Optional.of(sessaoVotacao));

        ReflectionTestUtils.setField(votoService, "votoRepository", votoRepository);
        ReflectionTestUtils.setField(votoService, "associadoRepository", associadoRepository);
        ReflectionTestUtils.setField(votoService, "cpfValidationService", cpfValidationService);
        ReflectionTestUtils.setField(sessaoVotacaoService, "sessaoVotacaoRepository", sessaoVotacaoRepository);

        votoService.registrarVoto(1L, 1L, VotoEscolhido.SIM);

        resultadoVotacaoService.obterResultadoVotacao(1L);

        verify(sessaoVotacaoRepository, times(2)).findByIdWithVotos(1L);
        verify(cpfValidationService, times(1)).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveInvalidarCacheAoCriarSessao() {
        final Pauta pauta = criarPauta();
        final SessaoVotacao sessaoVotacao = criarSessaoVotacao(pauta);

        when(sessaoVotacaoRepository.findByIdWithVotos(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoVotacao);

        resultadoVotacaoService.obterResultadoVotacao(1L);

        final SessaoVotacao novaSessaoVotacao = SessaoVotacao.builder()
                .id(2L)
                .pauta(pauta)
                .inicio(sessaoVotacao.getInicio().plusDays(1))
                .fim(sessaoVotacao.getFim().plusDays(2))
                .build();
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(novaSessaoVotacao);

        ReflectionTestUtils.setField(sessaoVotacaoService, "pautaRepository", pautaRepository);
        sessaoVotacaoService.criarSessao(1L);

        resultadoVotacaoService.obterResultadoVotacao(1L);

        verify(sessaoVotacaoRepository, times(2)).findByIdWithVotos(1L);
    }

    private Pauta criarPauta() {
        return Pauta.builder().id(1L).nome("Pauta 1").descricao("Descrição da Pauta 1").build();
    }

    private SessaoVotacao criarSessaoVotacao(final Pauta pauta) {
        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plusDays(1);
        return SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .inicio(inicio)
                .fim(fim)
                .votos(criarVotos(null))
                .build();
    }

    private Associado criarAssociado() {
        return Associado.builder().id(1L).cpf("12345678900").build();
    }

    private List<Voto> criarVotos(final SessaoVotacao sessaoVotacao) {
        final Associado associado = criarAssociado();
        final Voto voto1 = Voto.builder().id(1L).sessaoVotacao(sessaoVotacao).associado(associado).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto voto2 = Voto.builder().id(2L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(2L).build()).votoEscolhido(VotoEscolhido.NAO).build();
        if (sessaoVotacao != null) {
            voto1.setSessaoVotacao(sessaoVotacao);
            voto2.setSessaoVotacao(sessaoVotacao);
        }
        return List.of(voto1, voto2);
    }

    private List<Voto> criarVotosComVotosSimENao(final SessaoVotacao sessaoVotacao) {
        final Voto votoSim1 = Voto.builder().id(1L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(1L).build()).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto votoSim2 = Voto.builder().id(2L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(2L).build()).votoEscolhido(VotoEscolhido.SIM).build();
        final Voto votoNao = Voto.builder().id(3L).sessaoVotacao(sessaoVotacao).associado(Associado.builder().id(3L).build()).votoEscolhido(VotoEscolhido.NAO).build();
        if (sessaoVotacao != null) {
            votoSim1.setSessaoVotacao(sessaoVotacao);
            votoSim2.setSessaoVotacao(sessaoVotacao);
            votoNao.setSessaoVotacao(sessaoVotacao);
        }
        return List.of(votoSim1, votoSim2, votoNao);
    }
}