package com.example.cooperativa.service;

import com.example.cooperativa.dto.DetalheVotoDTO;
import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResultadoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final VotoRepository votoRepository;

    public Optional<ResultadoVotacaoDTO> obterResultadoVotacao(final Long sessaoVotacaoId) {
        final SessaoVotacao sessaoVotacao = getSessaoVotacao(sessaoVotacaoId);

        final List<Voto> votos = obterVotosPorSessao(sessaoVotacao);

        return Optional.of(calcularResultado(sessaoVotacao, votos));
    }

    private SessaoVotacao getSessaoVotacao(Long sessaoVotacaoId) {
        return sessaoVotacaoRepository.findById(sessaoVotacaoId)
                .orElseThrow(() -> new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + sessaoVotacaoId));
    }

    private List<Voto> obterVotosPorSessao(final SessaoVotacao sessaoVotacao) {
        return votoRepository.findBySessaoVotacao(sessaoVotacao);
    }

    private ResultadoVotacaoDTO calcularResultado(final SessaoVotacao sessaoVotacao,
                                                  final List<Voto> votos) {
        final long votosSim = votos.stream().filter(voto -> voto.getVotoEscolhido() == VotoEscolhido.SIM).count();
        final long votosNao = votos.size() - votosSim;

        final List<DetalheVotoDTO> detalhesVotos = obterDetalheDosVotos(votos);
        final String status = sessaoVotacao.isEncerrada() ? "encerrada" : "ativa";

        return ResultadoVotacaoDTO.builder()
                .sessaoVotacaoId(sessaoVotacao.getId())
                .pautaId(sessaoVotacao.getPauta().getId())
                .status(status)
                .votosSim(votosSim)
                .votosNao(votosNao)
                .detalhesVotos(detalhesVotos)
                .build();
    }

    private List<DetalheVotoDTO> obterDetalheDosVotos(final List<Voto> votos) {
        return votos.stream()
                .map(voto -> DetalheVotoDTO.builder()
                        .associadoId(voto.getAssociado().getId())
                        .votoEscolhido(voto.getVotoEscolhido().name())
                        .build())
                .toList();
    }
}