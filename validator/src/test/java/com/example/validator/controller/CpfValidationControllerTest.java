package com.example.validator.controller;

import com.example.validator.dto.CpfValidationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CpfValidationController.class)
class CpfValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void validarCpfDeveRetornarAbleToVoteEUnableToVote() throws Exception {
        final Set<String> statusSet = new HashSet<>();
        final String cpf = "12345678901";

        while (!statusSet.contains("ABLE_TO_VOTE") || !statusSet.contains("UNABLE_TO_VOTE")) {
            final String response = mockMvc.perform(get("/users/{cpf}", cpf))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            final CpfValidationResponseDTO dto = objectMapper.readValue(response, CpfValidationResponseDTO.class);
            statusSet.add(dto.getStatus());
        }

        assertThat(statusSet).contains("ABLE_TO_VOTE", "UNABLE_TO_VOTE");
    }
}