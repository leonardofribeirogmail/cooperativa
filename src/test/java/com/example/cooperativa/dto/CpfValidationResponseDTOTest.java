package com.example.cooperativa.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CpfValidationResponseDTOTest {

    @Test
    void deveConstruirCpfValidationResponseDTOComBuilder() {
        final CpfValidationResponseDTO response = CpfValidationResponseDTO
                .builder().status("test").build();
        assertNotNull(response);
        assertNotNull(response.getStatus());
    }

    @Test
    void deveConstruirCpfValidationResponseDTOSemArgumento() {
        final CpfValidationResponseDTO response = new CpfValidationResponseDTO();
        assertNotNull(response);
        assertNull(response.getStatus());
    }
}