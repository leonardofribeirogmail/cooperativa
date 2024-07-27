package com.example.cooperativa.service;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
import com.example.cooperativa.dto.VotoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.AssociadoNotFoundException;
import com.example.cooperativa.exception.SessaoVotacaoEncerradaException;
import com.example.cooperativa.exception.VotoDuplicadoException;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final AssociadoRepository associadoRepository;
    private final VotoRepository votoRepository;
    private final CPFValidationService cpfValidationService;
    private final SessaoVotacaoService sessaoVotacaoService;

    public VotoDTO registrarVoto(final Long sessaoVotacaoId,
                                 final Long associadoId,
                                 final VotoEscolhido votoEscolhido) {

        sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId);

        final Associado associado = getAssociado(associadoId);

        cpfValidationService.validarCpfNoServicoExterno(associado.getCpf());

        final SessaoVotacao sessaoVotacao = getSessaoVotacao(sessaoVotacaoId);

        validarVotoDuplicado(associado, sessaoVotacao);
        validarTempoDeSessao(sessaoVotacaoId, sessaoVotacao);

        final Voto novoVoto = Voto.builder()
                .sessaoVotacao(sessaoVotacao)
                .associado(associado)
                .votoEscolhido(votoEscolhido)
                .build();

        final Voto votoSalvo = votoRepository.save(novoVoto);

        return VotoDTO.builder()
                .id(votoSalvo.getId())
                .sessaoVotacaoId(votoSalvo.getSessaoVotacao().getId())
                .associadoId(votoSalvo.getAssociado().getId())
                .votoEscolhido(votoSalvo.getVotoEscolhido())
                .build();
    }

    private Associado getAssociado(final Long associadoId) {
        return associadoRepository.findById(associadoId)
                .orElseThrow(getAssociadoNotFoundExceptionSupplier(associadoId));
    }

    private Supplier<AssociadoNotFoundException> getAssociadoNotFoundExceptionSupplier(final Long associadoId) {
        return () -> new AssociadoNotFoundException("Associado não encontrado com ID: " + associadoId);
    }

    private SessaoVotacao getSessaoVotacao(final Long sessaoVotacaoId) {
        final SessaoVotacaoDTO sessaoVotacaoDTO = sessaoVotacaoService.obterSessaoPorId(sessaoVotacaoId);
        return SessaoVotacao.builder()
                .id(sessaoVotacaoDTO.id())
                .inicio(sessaoVotacaoDTO.inicio())
                .fim(sessaoVotacaoDTO.fim())
                .encerrada(sessaoVotacaoDTO.encerrada())
                .build();
    }

    private void validarVotoDuplicado(final Associado associado, final SessaoVotacao sessaoVotacao) {
        final boolean votoExistente = votoRepository.existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado);
        if (votoExistente) {
            throw new VotoDuplicadoException("Associado já votou nesta sessão de votação.");
        }
    }

    private void validarTempoDeSessao(final Long sessaoVotacaoId, final SessaoVotacao sessaoVotacao) {
        if (sessaoVotacao.isEncerrada()) {
            throw new SessaoVotacaoEncerradaException("Sessão de votação encerrada com ID: " + sessaoVotacaoId);
        }
    }
}