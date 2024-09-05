package com.example.cooperativa.service;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
import com.example.cooperativa.exception.PautaNotFoundException;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.PautaRepository;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.example.cooperativa.util.CacheAlias.RESULTADO_VOTACAO;
import static com.example.cooperativa.util.CacheAlias.SESSOES;

@Service
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;
    private final Long schedulerUpdateTime;

    public SessaoVotacaoService(final SessaoVotacaoRepository sessaoVotacaoRepository,
                                final PautaRepository pautaRepository,
                                @Value("${scheduler.updateRate}") final Long schedulerUpdateTime) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.pautaRepository = pautaRepository;
        this.schedulerUpdateTime = schedulerUpdateTime;
    }

    @Transactional
    @CacheEvict(value = {SESSOES, RESULTADO_VOTACAO}, allEntries = true)
    public SessaoVotacaoDTO criarSessao(final Long pautaId) {
        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plus(schedulerUpdateTime, ChronoUnit.MILLIS);

        final Pauta pauta = getPauta(pautaId);

        SessaoVotacao sessaoVotacao = SessaoVotacao.builder()
                .pauta(pauta)
                .inicio(inicio)
                .fim(fim)
                .build();

        sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

        return getSessaoVotacaoDTO(sessaoVotacao);
    }

    @Transactional
    @Cacheable(SESSOES)
    public List<SessaoVotacaoDTO> listarSessoes() {
        final List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll();

        sessoes.stream()
                .filter(this::sessaoEstaExpirada)
                .forEach(this::encerrarSessaoEAtualizarCache);

        return sessoes.stream()
                .map(this::getSessaoVotacaoDTO)
                .toList();
    }

    @Transactional
    @Cacheable(value = SESSOES, key = "#id")
    public SessaoVotacaoDTO obterSessaoPorId(final Long id) {
        final Optional<SessaoVotacao> sessaoVotacaoOptional = sessaoVotacaoRepository.findById(id);

        sessaoVotacaoOptional
                .filter(this::sessaoEstaExpirada)
                .ifPresent(this::encerrarSessaoEAtualizarCache);

        return sessaoVotacaoOptional
                .map(this::getSessaoVotacaoDTO)
                .orElseThrow(getSessaoNaoEncontradaException(id));
    }

    @Transactional
    public SessaoVotacao encerrarSessaoSeExpirada(final Long sessaoVotacaoId) {
        final SessaoVotacao sessaoVotacao = sessaoVotacaoRepository.findById(sessaoVotacaoId)
                .orElseThrow(getSessaoNaoEncontradaException(sessaoVotacaoId));

        if (sessaoEstaExpirada(sessaoVotacao)) {
            encerrarSessaoEAtualizarCache(sessaoVotacao);
        }

        return sessaoVotacao;
    }

    @CacheEvict(value = {SESSOES, RESULTADO_VOTACAO}, key = "#sessaoVotacao.id")
    public void encerrarSessaoEAtualizarCache(final SessaoVotacao sessaoVotacao) {
        sessaoVotacaoRepository.save(sessaoVotacao);
    }

    private Supplier<SessaoVotacaoNotFoundException> getSessaoNaoEncontradaException(final Long id) {
        return () -> new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + id);
    }

    private Pauta getPauta(final Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNotFoundException("Pauta não encontrada com ID: " + pautaId));
    }

    private SessaoVotacaoDTO getSessaoVotacaoDTO(final SessaoVotacao sessaoVotacao) {
        return SessaoVotacaoDTO.builder()
                .id(sessaoVotacao.getId())
                .encerrada(sessaoVotacao.isEncerrada())
                .pautaId(sessaoVotacao.getPauta().getId())
                .inicio(sessaoVotacao.getInicio())
                .fim(sessaoVotacao.getFim())
                .build();
    }

    private boolean sessaoEstaExpirada(final SessaoVotacao sessaoVotacao) {
        if (!sessaoVotacao.isEncerrada() && sessaoVotacao.getFim().isBefore(LocalDateTime.now())) {
            sessaoVotacao.setEncerrada(true);
            return true;
        }

        return false;
    }
}