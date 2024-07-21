package com.example.cooperativa.controller;

import com.example.cooperativa.dto.VotoDTO;
import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/votos")
@Tag(name = "Voto", description = "API para operações relacionadas aos votos")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Registra um novo votoEscolhido")
    public ResponseEntity<VotoDTO> registrarVoto(@RequestParam Long sessaoVotacaoId,
                                                 @RequestParam Long associadoId,
                                                 @RequestParam VotoEscolhido votoEscolhido) {
        final VotoDTO votoDTO = votoService.registrarVoto(sessaoVotacaoId, associadoId, votoEscolhido);
        return ResponseEntity.status(CREATED).body(votoDTO);
    }
}