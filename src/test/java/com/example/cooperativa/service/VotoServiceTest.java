package com.example.cooperativa.service;

import com.example.cooperativa.dto.VotoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.*;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private CPFValidationService cpfValidationService;

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
        sessaoVotacao = SessaoVotacao.builder().id(sessaoVotacaoId).build();
    }

    @Test
    void deveRegistrarUmVoto() {
        Voto novoVoto = Voto.builder().sessaoVotacao(sessaoVotacao).associado(associado).votoEscolhido(votoEscolhido).build();

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoRepository.findById(sessaoVotacaoId)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.save(any(Voto.class))).thenReturn(novoVoto);
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        VotoDTO result = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);

        assertNotNull(result);
        assertEquals(sessaoVotacaoId, result.sessaoVotacaoId());
        assertEquals(associadoId, result.associadoId());
        assertEquals(votoEscolhido, result.votoEscolhido());

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoRepository).findById(sessaoVotacaoId);
        verify(votoRepository).save(any(Voto.class));
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoNaoForEncontrado() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.empty());

        assertThrowsAssociadoNotFoundException();

        verify(associadoRepository).findById(associadoId);
        verifyNoInteractions(sessaoVotacaoRepository, votoRepository, cpfValidationService);
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoForEncontrada() {
        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoRepository.findById(sessaoVotacaoId)).thenReturn(Optional.empty());
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsSessaoVotacaoNotFoundException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoRepository).findById(sessaoVotacaoId);
        verifyNoInteractions(votoRepository);
        verify(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoEstiverEncerrada() {
        sessaoVotacao.setEncerrada(true);

        when(associadoRepository.findById(associadoId)).thenReturn(Optional.of(associado));
        when(sessaoVotacaoRepository.findById(sessaoVotacaoId)).thenReturn(Optional.of(sessaoVotacao));
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsSessaoVotacaoEncerradaException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoRepository).findById(sessaoVotacaoId);
        verify(votoRepository).existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado);
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
        when(sessaoVotacaoRepository.findById(sessaoVotacaoId)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado)).thenReturn(true);
        doNothing().when(cpfValidationService).validarCpfNoServicoExterno(associado.getCpf());

        assertThrowsVotoDuplicadoException();

        verify(associadoRepository).findById(associadoId);
        verify(sessaoVotacaoRepository).findById(sessaoVotacaoId);
        verify(votoRepository).existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado);
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