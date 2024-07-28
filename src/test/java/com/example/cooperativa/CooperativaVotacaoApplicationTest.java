package com.example.cooperativa;

import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CooperativaVotacaoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "The application context should have loaded.");
    }

    @Test
    void deveSalvarPautaNoBancoH2() {
        final Pauta pauta = Pauta.builder()
                        .nome("Pauta de Teste")
                        .descricao("Descrição de Teste").build();

        final Pauta savedPauta = pautaRepository.save(pauta);

        assertNotNull(savedPauta.getId());

        final Pauta foundPauta = pautaRepository.findById(savedPauta.getId()).orElse(null);

        assertNotNull(foundPauta);
        assertEquals("Pauta de Teste", foundPauta.getNome());
        assertEquals("Descrição de Teste", foundPauta.getDescricao());
    }
}