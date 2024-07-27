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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @CacheEvict(value = "sessoes", allEntries = true)
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

    private Pauta getPauta(final Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNotFoundException("Pauta não encontrada com ID: " + pautaId));
    }

    @Cacheable("sessoes")
    public List<SessaoVotacaoDTO> listarSessoes() {
        final List<SessaoVotacao> sessoes = sessaoVotacaoRepository.findAll();
        return sessoes.stream()
                .map(this::getSessaoVotacaoDTO)
                .toList();
    }

    @Cacheable(value = "sessoes", key = "#id")
    public SessaoVotacaoDTO obterSessaoPorId(final Long id) {
        return sessaoVotacaoRepository.findById(id)
                .map(this::getSessaoVotacaoDTO)
                .orElseThrow(getSessaoNaoEncontradaException(id));
    }

    @CacheEvict(value = "sessoes", key = "#sessaoVotacaoId")
    public void encerrarSessaoSeExpirada(final Long sessaoVotacaoId) {
        final Optional<SessaoVotacao> sessaoVotacaoOpt = sessaoVotacaoRepository.findById(sessaoVotacaoId);
        final Consumer<SessaoVotacao> consumer = encerrarESalvarSessao();

        sessaoVotacaoOpt.filter(sessaoVotacao -> !sessaoVotacao.isEncerrada())
                .filter(sessaoVotacao -> sessaoVotacao.getFim().isBefore(LocalDateTime.now()))
                .ifPresent(consumer);
    }

    private Consumer<SessaoVotacao> encerrarESalvarSessao() {
        return sessaoVotacao -> {
            sessaoVotacao.setEncerrada(true);
            sessaoVotacaoRepository.save(sessaoVotacao);
        };
    }

    private Supplier<SessaoVotacaoNotFoundException> getSessaoNaoEncontradaException(final Long id) {
        return () -> new SessaoVotacaoNotFoundException("Sessão de votação não encontrada com ID: " + id);
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
}