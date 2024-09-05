package com.example.cooperativa.repository;

import com.example.cooperativa.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    @Query("SELECT s FROM SessaoVotacao s LEFT JOIN FETCH s.votos WHERE s.id = :id")
    Optional<SessaoVotacao> findByIdWithVotos(@Param("id") Long id);

    @Query("SELECT s FROM SessaoVotacao s WHERE s.fim < :now AND s.encerrada = false")
    List<SessaoVotacao> findByFimBeforeAndEncerradaFalse(@Param("now") LocalDateTime now);
}