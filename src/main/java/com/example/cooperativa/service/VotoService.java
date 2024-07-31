package com.example.cooperativa.service;

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

import static com.example.cooperativa.util.CacheAlias.RESULTADO_VOTACAO;
import static com.example.cooperativa.util.CacheAlias.SESSOES;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final AssociadoRepository associadoRepository;
    private final VotoRepository votoRepository;
    private final CPFValidationService cpfValidationService;
    private final SessaoVotacaoService sessaoVotacaoService;

    @CacheEvict(value = {SESSOES, RESULTADO_VOTACAO}, key = "#sessaoVotacaoId")
    public VotoDTO registrarVoto(final Long sessaoVotacaoId,
                                 final Long associadoId,
                                 final VotoEscolhido votoEscolhido) {

        final SessaoVotacao sessaoVotacao = sessaoVotacaoService.encerrarSessaoSeExpirada(sessaoVotacaoId);

        validarTempoDeSessao(sessaoVotacao);

        final Associado associado = getAssociado(associadoId);

        cpfValidationService.validarCpfNoServicoExterno(associado.getCpf());

        validarVotoDuplicado(associado, sessaoVotacao);

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

    private void validarVotoDuplicado(final Associado associado, final SessaoVotacao sessaoVotacao) {
        final boolean votoExistente = votoRepository.existsBySessaoVotacaoAndAssociado(sessaoVotacao, associado);
        if (votoExistente) {
            throw new VotoDuplicadoException("Associado já votou nesta sessão de votação.");
        }
    }

    private void validarTempoDeSessao(final SessaoVotacao sessaoVotacao) {
        if (sessaoVotacao.isEncerrada()) {
            throw new SessaoVotacaoEncerradaException("Sessão de votação encerrada com ID: " + sessaoVotacao.getId());
        }
    }
}