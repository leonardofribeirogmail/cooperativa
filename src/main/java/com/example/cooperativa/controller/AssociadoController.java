package com.example.cooperativa.controller;

import com.example.cooperativa.dto.AssociadoResponseDTO;
import com.example.cooperativa.dto.CriarAssociadoDTO;
import com.example.cooperativa.service.AssociadoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/associados")
@RequiredArgsConstructor
@Tag(name = "Associado", description = "API para cadastro e consulta de associados")
public class AssociadoController {

    private final AssociadoService associadoService;

    @PostMapping
    public ResponseEntity<AssociadoResponseDTO> criarAssociado(@RequestBody CriarAssociadoDTO criarAssociadoDTO) {
        return ResponseEntity.ok(associadoService.criarAssociado(criarAssociadoDTO));
    }

    @GetMapping
    public ResponseEntity<List<AssociadoResponseDTO>> listarAssociados() {
        return ResponseEntity.ok(associadoService.listarAssociados());
    }
}