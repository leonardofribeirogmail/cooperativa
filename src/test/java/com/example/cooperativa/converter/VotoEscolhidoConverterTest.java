package com.example.cooperativa.converter;

import com.example.cooperativa.enums.VotoEscolhido;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.model.SessaoVotacao;
import com.example.cooperativa.model.Voto;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.repository.PautaRepository;
import com.example.cooperativa.repository.SessaoVotacaoRepository;
import com.example.cooperativa.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VotoEscolhidoConverterTest {

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private AssociadoRepository associadoRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private PautaRepository pautaRepository;

    private Associado associado;
    private SessaoVotacao sessaoVotacao;

    @BeforeEach
    void setUp() {
        votoRepository.deleteAll();
        associadoRepository.deleteAll();
        sessaoVotacaoRepository.deleteAll();

        associado = associadoRepository.save(Associado.builder().cpf("12345678901").build());

        sessaoVotacao = sessaoVotacaoRepository.save(getSessaoVotacao());
    }

    @Test
    void deveConverterEArmazenarVotoEscolhidoSim() {
        final Voto voto = Voto.builder()
                .associado(associado)
                .sessaoVotacao(sessaoVotacao)
                .votoEscolhido(VotoEscolhido.SIM)
                .build();

        final Voto savedVoto = votoRepository.save(voto);
        assertNotNull(savedVoto.getId());

        final Voto foundVoto = votoRepository.findById(savedVoto.getId()).orElse(null);
        assertNotNull(foundVoto);
        assertEquals(VotoEscolhido.SIM, foundVoto.getVotoEscolhido());
    }

    @Test
    void deveConverterEArmazenarVotoEscolhidoNao() {
        final Voto voto = Voto.builder()
                .associado(associado)
                .sessaoVotacao(sessaoVotacao)
                .votoEscolhido(VotoEscolhido.NAO)
                .build();

        final Voto savedVoto = votoRepository.save(voto);
        assertNotNull(savedVoto.getId());

        final Voto foundVoto = votoRepository.findById(savedVoto.getId()).orElse(null);
        assertNotNull(foundVoto);
        assertEquals(VotoEscolhido.NAO, foundVoto.getVotoEscolhido());
    }

    @Test
    void deveRetornarFalseParaVotoEscolhidoNulo() {
        final VotoEscolhidoConverter converter = new VotoEscolhidoConverter();
        assertFalse(converter.convertToDatabaseColumn(null));
    }

    @Test
    void deveRetornarVotoEscolhidoNaoNulo() {
        final VotoEscolhidoConverter converter = new VotoEscolhidoConverter();
        assertNotNull(converter.convertToEntityAttribute(Boolean.TRUE));
    }

    private SessaoVotacao getSessaoVotacao() {
        final LocalDateTime inicio = LocalDateTime.now();
        final LocalDateTime fim = inicio.plusMinutes(1);
        final Pauta pauta = pautaRepository.save(Pauta.builder().build());
        return SessaoVotacao.builder()
                .pauta(pauta)
                .inicio(inicio)
                .fim(fim)
                .build();
    }
}