package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Representação de uma Pauta")
public record PautaDTO(
        @Schema(description = "ID único da pauta", example = "1")
        Long id,

        @Schema(description = "Nome da pauta", example = "Pauta de Teste")
        String nome,

        @Schema(description = "Descrição da pauta", example = "Descrição importante de uma Pauta")
        String descricao
) {}