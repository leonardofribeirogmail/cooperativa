package com.example.validator.controller;

import com.example.validator.dto.CpfValidationResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class CpfValidationController {

    private final Random random = new Random();

    @GetMapping("/users/{cpf}")
    public ResponseEntity<CpfValidationResponseDTO> validarCpf(@PathVariable String cpf) {
        final String status = random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE";
        return ResponseEntity.ok(new CpfValidationResponseDTO(status));
    }
}
