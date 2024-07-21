package com.example.cooperativa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Criação de uma nova Pauta")
public record CriarPautaDTO(
    @Schema(description = "Nome da pauta", example = "Pauta de Teste")
    @NotEmpty @NotNull String nome,

    @Schema(description = "Descrição da pauta", example = "Descrição importante de uma Pauta")
    @NotEmpty @NotNull String descricao
){}
