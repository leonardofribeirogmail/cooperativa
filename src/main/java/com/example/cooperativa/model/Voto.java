package com.example.cooperativa.model;

import com.example.cooperativa.converter.VotoEscolhidoConverter;
import com.example.cooperativa.enums.VotoEscolhido;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voto", indexes = {
        @Index(name = "idx_voto_associado_id", columnList = "associado_id"),
        @Index(name = "idx_voto_sessao_id", columnList = "sessao_votacao_id")
})
public class Voto {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "associado_id", referencedColumnName = "id", nullable = false)
    private Associado associado;

    @Column(name = "voto_escolhido", nullable = false)
    @Convert(converter = VotoEscolhidoConverter.class)
    private VotoEscolhido votoEscolhido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_votacao_id", nullable = false)
    private SessaoVotacao sessaoVotacao;
}