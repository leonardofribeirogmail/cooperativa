package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Tabela para criação de um novo Associado ou consulta por CPF")
public record CriarAssociadoDTO(
        @NotNull
        @Schema(description = "CPF do associado", example = "12345678909")
        String cpf
) {
        public CriarAssociadoDTO {
                cpf = cpf.replaceAll("\\D", "");
        }
}