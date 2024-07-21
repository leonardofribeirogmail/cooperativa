package com.example.cooperativa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "Representação de uma Sessão de Votação")
public record SessaoVotacaoDTO(
        @Schema(description = "ID único da sessão de votação", example = "1")
        Long id,

        @Schema(description = "ID da pauta relacionada à sessão de votação", example = "1")
        Long pautaId,

        @Schema(description = "Indica se a sessão foi encerrada", example = "false")
        boolean encerrada,

        @Schema(description = "Data e hora de início da sessão", example = "2024-07-18T14:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime inicio,

        @Schema(description = "Data e hora de fim da sessão", example = "2024-07-18T15:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        LocalDateTime fim
) {}