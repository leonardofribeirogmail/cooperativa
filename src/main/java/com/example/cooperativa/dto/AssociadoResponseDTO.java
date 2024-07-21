package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Tabela para resposta de um Associado")
public record AssociadoResponseDTO(
        @Schema(description = "ID único do associado", example = "1")
        Long id,

        @Schema(description = "CPF do associado", example = "12345678901")
        String cpf
) {}