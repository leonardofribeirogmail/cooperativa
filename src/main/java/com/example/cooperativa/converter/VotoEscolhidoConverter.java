package com.example.cooperativa.converter;

import com.example.cooperativa.enums.VotoEscolhido;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;

public class VotoEscolhidoConverter implements AttributeConverter<VotoEscolhido, Boolean> {
    @Override
    public Boolean convertToDatabaseColumn(final VotoEscolhido votoEscolhido) {
        if(Objects.isNull(votoEscolhido)) {
            return false;
        }
        return "SIM".equals(votoEscolhido.name());
    }

    @Override
    public VotoEscolhido convertToEntityAttribute(final Boolean valor) {
        return Boolean.TRUE.equals(valor) ? VotoEscolhido.SIM : VotoEscolhido.NAO;
    }
}
