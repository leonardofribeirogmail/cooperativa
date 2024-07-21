package com.example.cooperativa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CpfValidationResponseDTO {
    @JsonProperty("status")
    private String status;

    @Override
    public String toString() {
        return "{\"status\":\"" + status + "\"}";
    }
}
