package com.example.cooperativa.service;

import com.example.cooperativa.dto.VotoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.*;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private CPFValidationService cpfValidationService;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @InjectMocks
    private VotoService votoService;

    private Long sessaoVotacaoId;
    private Long associadoId;
    private String cpf;
    private VotoEscolhido votoEscolhido;
    private Associado associado;
    private SessaoVotacao sessaoVotacao;

    @BeforeEach
    void setUp() {
        sessaoVotacaoId = 1L;
        associadoId = 1L;
        cpf = "12345678901";
        votoEscolhido = VotoEscolhido.SIM;
        associado = Associado.builder().id(associadoId).cpf(cpf).build();
        sessaoVotacao = criarSessaoVotacao(sessaoVotacaoId);
    }

    @Test
    void deveRegistrarUmVoto() {
        final Voto novoVoto = criarVoto(sessaoVotacao, associado, votoEscolhido);

        mockSessaoEAssociado();
        when(votoRepository.save(any(Voto.class))).thenReturn(novoVoto);

        final VotoDTO result = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);

        assertNotNull(result);
        assertEquals(sessaoVotacaoId, result.sessaoVotacaoId());
        assertEquals(associadoId, result.associadoId());
        assertEquals(votoEscolhido, result.votoEscolhido());

        verificarInteracoesRegistroDeVoto();
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoNaoForEncontrado() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());

        assertThrowsAssociadoNotFoundException();

        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(votoRepository, cpfValidationService);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoEstiverEncerrada() {
        sessaoVotacao.setEncerrada(true);
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);

        assertThrowsSessaoVotacaoEncerradaException();

        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoPodeVotar() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        doThrow(new InvalidCPFException("Associado não pode votar: " + cpf))
                .when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsInvalidCPFException();

        verificarInteracoesAssociadoECpfValidation();
    }

    @Test
    void deveLancarExcecaoQuandoServicoExternoFalha() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        doThrow(new CpfValidationException("Falha na validação do CPF"))
                .when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsCpfValidationException();

        verificarInteracoesAssociadoECpfValidation();
    }

    @Test
    void deveLancarExcecaoQuandoVotoDuplicado() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(votoRepository.existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado)).thenReturn(true);

        assertThrowsVotoDuplicadoException();

        verify(associadoRepository).findById(associadoId);
        verify(votoRepository).existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado);
        verifyNoMoreInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoDeVotacaoNaoForEncontrada() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenThrow(new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + sessaoVotacaoId));

        assertThrows(SessaoVotacaoNotFoundException.class, getRegistrarVotoExecution());

        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verifyNoInteractions(associadoRepository, votoRepository, cpfValidationService);
    }

    @Test
    void deveValidarCpfComSucesso() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(votoRepository.save(any(Voto.class))).thenReturn(Voto.builder().id(1L).sessaoVotacao(sessaoVotacao).associado(associado).votoEscolhido(votoEscolhido).build());
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        final VotoDTO result = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);

        assertNotNull(result);
        assertEquals(sessaoVotacaoId, result.sessaoVotacaoId());
        assertEquals(associadoId, result.associadoId());
        assertEquals(votoEscolhido, result.votoEscolhido());

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verify(votoRepository).save(any(Voto.class));
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveRegistrarVotoQuandoSessaoEstaAtiva() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(votoRepository.existsBySessaoVotacaoAndAssociado(any(SessaoVotacao.class), eq(associado))).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenReturn(Voto.builder().id(1L).sessaoVotacao(sessaoVotacao).associado(associado).votoEscolhido(votoEscolhido).build());
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        final VotoDTO result = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);

        assertNotNull(result);
        assertEquals(sessaoVotacaoId, result.sessaoVotacaoId());
        assertEquals(associadoId, result.associadoId());
        assertEquals(votoEscolhido, result.votoEscolhido());

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verify(votoRepository).existsBySessaoVotacaoAndAssociado(any(SessaoVotacao.class), eq(associado));
        verify(votoRepository).save(any(Voto.class));
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    private void assertThrowsAssociadoNotFoundException() {
        assertThrows(AssociadoNotFoundException.class, getRegistrarVotoExecution());
    }

    private void assertThrowsSessaoVotacaoEncerradaException() {
        assertThrows(SessaoVotacaoEncerradaException.class, getRegistrarVotoExecution());
    }

    private void assertThrowsInvalidCPFException() {
        assertThrows(InvalidCPFException.class, getRegistrarVotoExecution());
    }

    private void assertThrowsCpfValidationException() {
        assertThrows(CpfValidationException.class, getRegistrarVotoExecution());
    }

    private void assertThrowsVotoDuplicadoException() {
        assertThrows(VotoDuplicadoException.class, getRegistrarVotoExecution());
    }

    private Executable getRegistrarVotoExecution() {
        return () -> votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);
    }

    private void mockSessaoEAssociado() {
        when(sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId)).thenReturn(sessaoVotacao);
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    private void verificarInteracoesRegistroDeVoto() {
        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verify(associadoRepository).findById(associadoId);
        verify(votoRepository).save(any(Voto.class));
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    private void verificarInteracoesAssociadoECpfValidation() {
        verify(sessaoVotacaoService).encerrarSessaoSeExpirada(sessaoVotacaoId);
        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    private SessaoVotacao criarSessaoVotacao(final Long sessaoVotacaoId) {
        return SessaoVotacao.builder()
                .id(sessaoVotacaoId)
                .inicio(LocalDateTime.now())
                .fim(LocalDateTime.now().plusMinutes(1))
                .encerrada(false)
                .build();
    }

    private Voto criarVoto(final SessaoVotacao sessaoVotacao, final Associado associado, final VotoEscolhido votoEscolhido) {
        return Voto.builder()
                .sessaoVotacao(sessaoVotacao)
                .associado(associado)
                .votoEscolhido(votoEscolhido)
                .build();
    }
}