package com.example.cooperativa.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe utilitária para definir e obter aliases de cache.
 */
@Slf4j
public class CacheAlias {

    public static final String SESSOES = "sessoes";
    public static final String ASSOCIADOS = "associados";
    public static final String PAUTAS = "pautas";
    public static final String RESULTADO_VOTACAO = "resultadoVotacao";
    public static final String CPF_VALIDATION = "cpfValidation";

    private CacheAlias(){
        throw new UnsupportedOperationException("CacheAlias é uma classe utilitária e não deve ser instanciada.");
    }

    private static final String[] ALIASES = {
            SESSOES, ASSOCIADOS, PAUTAS, RESULTADO_VOTACAO, CPF_VALIDATION
    };

    /**
     * Obtém os aliases de cache definidos nesta classe.
     *
     * @return um array de strings contendo os aliases de cache.
     */
    public static String[] getAliases() {
        return ALIASES.clone();
    }
}