package com.example.cooperativa.service;

import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AtualizacaoSessaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    public void atualizarSessoesEncerradas() {
        final List<SessaoVotacao> sessoesExpiradas = sessaoVotacaoRepository
                .findByFimBeforeAndEncerradaFalse(LocalDateTime.now());

        log.debug("{} sessao(es) foram encontradas e precisam ser encerradas", sessoesExpiradas.size());

        sessoesExpiradas.forEach(sessao -> {
            sessao.setEncerrada(true);
            sessaoVotacaoRepository.save(sessao);
            log.debug("Sessao encerrada por schedule {}", sessao.getId());
        });
    }

    @Transactional
    @EventListener(ContextRefreshedEvent.class)
    public void atualizarSessoesEncerradasAoIniciar() {
        atualizarSessoesEncerradas();
    }
}
