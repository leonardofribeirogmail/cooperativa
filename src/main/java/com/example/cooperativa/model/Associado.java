package com.example.cooperativa.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "associado", indexes = {
        @Index(name = "idx_associado_cpf", columnList = "cpf", unique = true)
})
public class Associado {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cpf;
}