package com.example.cooperativa.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * Entidade que representa uma pauta de votação.
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pauta", indexes = {
        @Index(name = "idx_pauta_nome", columnList = "nome"),
        @Index(name = "idx_pauta_descricao", columnList = "descricao")
})
public class Pauta {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
}
