package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Representação do Resultado de uma Votação")
public record ResultadoVotacaoDTO(
        @Schema(description = "ID da sessão de votação", example = "1")
        Long sessaoVotacaoId,

        @Schema(description = "Status da sessão atual", example = "encerrada")
        String status,

        @Schema(description = "ID da pauta relacionada à votação", example = "1")
        Long pautaId,

        @Schema(description = "Número de votos SIM", example = "10")
        long votosSim,

        @Schema(description = "Número de votos NÃO", example = "5")
        long votosNao,

        @Schema(description = "Detalhes dos votos")
        List<DetalheVotoDTO> detalhesVotos
) {}