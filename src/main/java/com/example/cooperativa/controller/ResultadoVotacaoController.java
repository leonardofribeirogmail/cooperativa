package com.example.cooperativa.controller;

import com.example.cooperativa.dto.ResultadoVotacaoDTO;
import com.example.cooperativa.service.ResultadoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/resultados")
@Tag(name = "ResultadoVotacao", description = "API para operações relacionadas aos resultados das votações")
@RequiredArgsConstructor
public class ResultadoVotacaoController {

    private final ResultadoVotacaoService resultadoVotacaoService;

    @GetMapping("/{sessaoVotacaoId}")
    @Operation(summary = "Obtém o resultado da votação de uma sessão específica")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultadoVotacao(@PathVariable Long sessaoVotacaoId) {
        return resultadoVotacaoService.obterResultadoVotacao(sessaoVotacaoId)
                .map(resultado -> new ResponseEntity<>(resultado, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}