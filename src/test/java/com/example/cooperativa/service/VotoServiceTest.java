package com.example.cooperativa.service;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
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
    private SessaoVotacaoDTO sessaoVotacaoDTO;

    @BeforeEach
    void setUp() {
        sessaoVotacaoId = 1L;
        associadoId = 1L;
        cpf = "12345678901";
        votoEscolhido = VotoEscolhido.SIM;
        associado = Associado.builder().id(associadoId).cpf(cpf).build();
        sessaoVotacao = SessaoVotacao.builder().id(sessaoVotacaoId).build();

        sessaoVotacaoDTO = SessaoVotacaoDTO.builder()
                .id(sessaoVotacaoId)
                .inicio(LocalDateTime.now())
                .fim(LocalDateTime.now().plusMinutes(1))
                .encerrada(false).build();
    }

    @Test
    void deveRegistrarUmVoto() {
        Voto novoVoto = Voto.builder().sessaoVotacao(sessaoVotacao).associado(associado).votoEscolhido(votoEscolhido).build();

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.obterSessaoPorId(sessaoVotacaoId)).thenReturn(sessaoVotacaoDTO);
        when(votoRepository.save(any(Voto.class))).thenReturn(novoVoto);
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        VotoDTO result = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);

        assertNotNull(result);
        assertEquals(sessaoVotacaoId, result.sessaoVotacaoId());
        assertEquals(associadoId, result.associadoId());
        assertEquals(votoEscolhido, result.votoEscolhido());

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).obterSessaoPorId(sessaoVotacaoId);
        verify(votoRepository).save(any(Voto.class));
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoNaoForEncontrado() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());

        assertThrowsAssociadoNotFoundException();

        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(votoRepository, cpfValidationService);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoForEncontrada() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.obterSessaoPorId(sessaoVotacaoId)).thenThrow(new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + sessaoVotacaoId));
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsSessaoVotacaoNotFoundException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).obterSessaoPorId(sessaoVotacaoId);
        verifyNoInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoEstiverEncerrada() {
        sessaoVotacaoDTO = SessaoVotacaoDTO.builder().id(sessaoVotacaoId).encerrada(true).build();

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.obterSessaoPorId(sessaoVotacaoId)).thenReturn(sessaoVotacaoDTO);
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsSessaoVotacaoEncerradaException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).obterSessaoPorId(sessaoVotacaoId);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoPodeVotar() {
        doThrow(new InvalidCPFException("Associado não pode votar: " + cpf))
                .when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));

        assertThrowsInvalidCPFException();

        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoServicoExternoFalha() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        doThrow(new CpfValidationException("Falha na validação do CPF"))
                .when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsCpfValidationException();

        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoVotoDuplicado() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoService.obterSessaoPorId(sessaoVotacaoId)).thenReturn(sessaoVotacaoDTO);
        when(votoRepository.existsBySessaoVotacaoAndAssociado(any(SessaoVotacao.class), eq(associado))).thenReturn(true);
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsVotoDuplicadoException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoService).obterSessaoPorId(sessaoVotacaoId);
        verify(votoRepository).existsBySessaoVotacaoAndAssociado(any(SessaoVotacao.class), eq(associado));
        verifyNoMoreInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    private void assertThrowsAssociadoNotFoundException() {
        assertThrows(AssociadoNotFoundException.class, getRegistrarVotoExecution());
    }

    private void assertThrowsSessaoVotacaoNotFoundException() {
        assertThrows(SessaoVotacaoNotFoundException.class, getRegistrarVotoExecution());
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
}
