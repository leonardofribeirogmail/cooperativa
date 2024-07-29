package com.example.cooperativa.controller;

import com.example.cooperativa.dto.CriarPautaDTO;
import com.example.cooperativa.dto.PautaDTO;
import com.example.cooperativa.exception.SessaoVotacaoNotFoundException;
import com.example.cooperativa.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * Controlador para gerenciar endpoints relacionados à Pauta.
 */
@RestController
@RequestMapping("/api/pautas")
@Tag(name = "Pauta", description = "API para operações relacionadas a pautas de votação")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    @Operation(summary = "Cria uma nova pauta")
    public ResponseEntity<PautaDTO> criarPauta(@RequestBody CriarPautaDTO criarPautaDTO) {
        final PautaDTO novaPautaDTO = pautaService.criarPauta(criarPautaDTO);
        return new ResponseEntity<>(novaPautaDTO, CREATED);
    }

    @GetMapping
    @Operation(summary = "Lista todas as pautas")
    public ResponseEntity<List<PautaDTO>> listarPautas() {
        final List<PautaDTO> pautas = pautaService.listarPautas();
        return new ResponseEntity<>(pautas, OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtém uma pauta pelo ID")
    public ResponseEntity<PautaDTO> obterPautaPorId(@PathVariable Long id) {
        final PautaDTO pautaDTO = pautaService.obterPautaPorId(id)
                .orElseThrow(getSessaoVotacaoNotFoundExceptionSupplier(id));
        return new ResponseEntity<>(pautaDTO, OK);
    }

    private Supplier<SessaoVotacaoNotFoundException> getSessaoVotacaoNotFoundExceptionSupplier(Long id) {
        return () -> new SessaoVotacaoNotFoundException("Sessão de votação ID " + id + " não encontrada");
    }
}