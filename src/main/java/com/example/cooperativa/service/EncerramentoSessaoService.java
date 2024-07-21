package com.example.cooperativa.service;

import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EncerramentoSessaoService {
    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public void encerrarSessoesExpiradas() {
        final List<SessaoVotacao> sessoesExpiradas = sessaoVotacaoRepository.findByFimBeforeAndEncerradaFalse(LocalDateTime.now());
        for (SessaoVotacao sessao : sessoesExpiradas) {
            sessao.setEncerrada(true);
            sessaoVotacaoRepository.save(sessao);
        }
    }
}