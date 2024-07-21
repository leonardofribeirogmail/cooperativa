package com.example.cooperativa.dto;

import com.example.cooperativa.enums.VotoEscolhido;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Representação de um Voto")
public record VotoDTO(
        @Schema(description = "ID único do voto", example = "1")
        Long id,

        @Schema(description = "ID da sessão de votação", example = "1")
        Long sessaoVotacaoId,

        @Schema(description = "ID do associado que realizou o voto", example = "1")
        Long associadoId,

        @Schema(description = "Voto escolhido", example = "SIM")
        VotoEscolhido votoEscolhido
) {}