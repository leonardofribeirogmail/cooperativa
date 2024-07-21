package com.example.cooperativa.repository;

import com.example.cooperativa.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    List<SessaoVotacao> findByFimBeforeAndEncerradaFalse(final LocalDateTime now);
}
