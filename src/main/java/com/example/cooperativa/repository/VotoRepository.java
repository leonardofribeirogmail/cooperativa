package com.example.cooperativa.repository;

import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    List<Voto> findBySessaoVotacao(final SessaoVotacao sessaoVotacao);
    boolean existsBySessaoVotacaoAndAssociado(final SessaoVotacao sessaoVotacao, final Associado associado);
}
