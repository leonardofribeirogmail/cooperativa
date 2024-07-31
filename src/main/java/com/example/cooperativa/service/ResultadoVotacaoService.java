package com.example.cooperativa.service;

import com.example.cooperativa.dto.DetalheVotoDTO;
import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.example.cooperativa.util.CacheAlias.RESULTADO_VOTACAO;

@Service
@RequiredArgsConstructor
public class ResultadoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Cacheable(value = RESULTADO_VOTACAO, key = "#sessaoVotacaoId")
    public Optional<ResultadoVotacaoDTO> obterResultadoVotacao(final Long sessaoVotacaoId) {
        final SessaoVotacao sessaoVotacao = buscarSessaoVotacaoPorIdComVotos(sessaoVotacaoId);
        return Optional.of(calcularResultado(sessaoVotacao));
    }

    private SessaoVotacao buscarSessaoVotacaoPorIdComVotos(final Long sessaoVotacaoId) {
        return sessaoVotacaoRepository.findByIdWithVotos(sessaoVotacaoId)
                .orElseThrow(getSessaoVotacaoNotFoundException(sessaoVotacaoId));
    }

    private ResultadoVotacaoDTO calcularResultado(final SessaoVotacao sessaoVotacao) {
        final List<Voto> votos = sessaoVotacao.getVotos();
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

    private Supplier<SessaoVotacaoNotFoundException> getSessaoVotacaoNotFoundException(final Long sessaoVotacaoId) {
        return () -> new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + sessaoVotacaoId);
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