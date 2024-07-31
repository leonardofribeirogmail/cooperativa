package com.example.cooperativa.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheAliasTest {

    @Test
    void deveRetornarTodosOsAliases() {
        final String[] expectedAliases = {
                CacheAlias.SESSOES,
                CacheAlias.ASSOCIADOS,
                CacheAlias.PAUTAS,
                CacheAlias.RESULTADO_VOTACAO,
                CacheAlias.CPF_VALIDATION
        };

        final String[] aliases = CacheAlias.getAliases();
        assertArrayEquals(expectedAliases, aliases);
    }

    @Test
    void deveLancarUnsupportedOperationExceptionNoConstrutor() throws NoSuchMethodException {
        final Constructor<CacheAlias> constructor = CacheAlias.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertThrows(UnsupportedOperationException.class, () -> {
            throw thrown.getCause();
        });
    }
}