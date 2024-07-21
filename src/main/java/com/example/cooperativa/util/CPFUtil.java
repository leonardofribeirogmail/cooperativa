package com.example.cooperativa.util;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.stereotype.Component;

@Component
public class CPFUtil extends CPFValidator {
    public boolean isCpfValido(final String cpf) {
        try {
            assertValid(cpf);
            return true;
        } catch (InvalidStateException e) {
            return false;
        }
    }
}