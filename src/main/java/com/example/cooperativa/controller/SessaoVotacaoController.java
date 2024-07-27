package com.example.cooperativa.controller;

import com.example.cooperativa.dto.SessaoVotacaoDTO;
import com.example.cooperativa.service.SessaoVotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sessoes")
@Tag(name = "SessaoVotacao", description = "API para operações relacionadas às sessões de votação")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping
    @Operation(summary = "Cria uma nova sessão de votação")
    public ResponseEntity<SessaoVotacaoDTO> criarSessao(@RequestParam Long pautaId) {
        final SessaoVotacaoDTO sessaoVotacaoDTO = sessaoVotacaoService.criarSessao(pautaId);
        return new ResponseEntity<>(sessaoVotacaoDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lista todas as sessões de votação")
    public ResponseEntity<List<SessaoVotacaoDTO>> listarSessoes() {
        final List<SessaoVotacaoDTO> sessoes = sessaoVotacaoService.listarSessoes();
        return new ResponseEntity<>(sessoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma sessão de votação pelo ID")
    public ResponseEntity<SessaoVotacaoDTO> obterSessaoPorId(@PathVariable Long id) {
        final Optional<SessaoVotacaoDTO> sessaoVotacaoDTO = Optional.ofNullable(sessaoVotacaoService.obterSessaoPorId(id));
        return sessaoVotacaoDTO
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}