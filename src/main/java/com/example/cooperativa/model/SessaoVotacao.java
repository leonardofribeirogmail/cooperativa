package com.example.cooperativa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sessao_votacao", indexes = {
        @Index(name = "idx_sessao_votacao_pauta_id", columnList = "pauta_id"),
        @Index(name = "idx_sessao_votacao_inicio", columnList = "inicio"),
        @Index(name = "idx_sessao_votacao_fim", columnList = "fim")
})
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Column(nullable = false)
    private boolean encerrada;

    @OneToMany(mappedBy = "sessaoVotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos;
}