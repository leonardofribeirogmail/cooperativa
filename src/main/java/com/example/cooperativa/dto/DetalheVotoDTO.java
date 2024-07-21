package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Detalhes de um voto")
public record DetalheVotoDTO(
        @Schema(description = "ID do associado que votou", example = "1")
        Long associadoId,

        @Schema(description = "Voto escolhido pelo associado", example = "SIM")
        String votoEscolhido
) {}