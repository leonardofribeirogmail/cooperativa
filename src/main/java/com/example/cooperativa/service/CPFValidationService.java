package com.example.cooperativa.service;

import com.example.cooperativa.dto.CpfValidationResponseDTO;
import com.example.cooperativa.exception.CpfValidationException;
import com.example.cooperativa.exception.InvalidCPFException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Service
public class CPFValidationService {

    private final RestTemplate restTemplate;
    private final String cpfValidatorUrl;

    public CPFValidationService(final RestTemplate restTemplate,
                                @Value("${cpf.validator.url}") final String cpfValidatorUrl) {
        this.restTemplate = restTemplate;
        this.cpfValidatorUrl = cpfValidatorUrl;
    }

    public void validarCpfNoServicoExterno(final String cpf) {

        try {
            final String url = cpfValidatorUrl.replace("{cpf}", cpf);
            final CpfValidationResponseDTO response = restTemplate.getForObject(url, CpfValidationResponseDTO.class);
            validarCpfValidationResponse(cpf, response);
            log.info("O associado está apto para votar: "+response);
        } catch (InvalidCPFException e) {
            throw new InvalidCPFException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new CpfValidationException("Timeout ao verificar o status de voto do CPF: " + cpf);
        } catch (Exception e) {
            throw new CpfValidationException(e.getCause(), "Não foi possível verificar o status de voto no momento: " + cpf);
        }
    }

    private void validarCpfValidationResponse(final String cpf,
                                              final CpfValidationResponseDTO response) {
        Optional.ofNullable(response)
        .map(CpfValidationResponseDTO::getStatus)
        .filter("UNABLE_TO_VOTE"::equals)
        .ifPresent(getInvalidCPFExceptionConsumer(cpf, response));
    }

    private Consumer<String> getInvalidCPFExceptionConsumer(final String cpf,
                                                            final CpfValidationResponseDTO response) {
        return status -> {
            log.warn("O associado não pode votar: "+response);
            throw new InvalidCPFException("Associado não pode votar: " + cpf);
        };
    }
}