package com.example.cooperativa.repository;

import com.example.cooperativa.model.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
    boolean existsByCpf(String cpf);
}