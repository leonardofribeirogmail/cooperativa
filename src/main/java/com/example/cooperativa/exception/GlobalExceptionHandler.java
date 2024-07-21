package com.example.cooperativa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PautaNotFoundException.class)
    public ResponseEntity<String> handlePautaNotFoundException(final PautaNotFoundException ex) {
        log.error("PautaNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(SessaoVotacaoNotFoundException.class)
    public ResponseEntity<String> handleSessaoVotacaoNotFoundException(final SessaoVotacaoNotFoundException ex) {
        log.error("SessaoVotacaoNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(AssociadoNotFoundException.class)
    public ResponseEntity<String> handleAssociadoNotFoundException(final AssociadoNotFoundException ex) {
        log.error("AssociadoNotFoundException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(InvalidCPFException.class)
    public ResponseEntity<String> handleInvalidCPFException(final InvalidCPFException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(CpfValidationException.class)
    public ResponseEntity<String> handleCpfValidationException(final CpfValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler(SessaoVotacaoEncerradaException.class)
    public ResponseEntity<String> handleSessaoVotacaoEncerradaException(final SessaoVotacaoEncerradaException ex) {
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(AssociadoExistenteException.class)
    public ResponseEntity<String> handleAssociadoExistenteException(final AssociadoExistenteException ex) {
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    public ResponseEntity<String> handleVotoDuplicadoException(final VotoDuplicadoException ex) {
        return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(final Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("Ocorreu um erro inesperado. Tente novamente mais tarde.", INTERNAL_SERVER_ERROR);
    }
}