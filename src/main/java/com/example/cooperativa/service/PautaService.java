package com.example.cooperativa.service;

import com.example.cooperativa.dto.CriarPautaDTO;
import com.example.cooperativa.dto.PautaDTO;
import com.example.cooperativa.model.Pauta;
import com.example.cooperativa.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.cooperativa.util.CacheAlias.PAUTAS;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    @CacheEvict(value = PAUTAS, allEntries = true)
    public PautaDTO criarPauta(final CriarPautaDTO criarPautaDTO) {
        final Pauta pauta = Pauta.builder()
                .nome(criarPautaDTO.nome())
                .descricao(criarPautaDTO.descricao())
                .build();
        final Pauta pautaCriada = pautaRepository.save(pauta);
        return getPautaDTO(pautaCriada);
    }

    @Cacheable(value = PAUTAS)
    public List<PautaDTO> listarPautas() {
        return pautaRepository.findAll().stream()
                .map(this::getPautaDTO)
                .toList();
    }

    @Cacheable(value = PAUTAS, key = "#id")
    public Optional<PautaDTO> obterPautaPorId(final Long id) {
        return pautaRepository.findById(id)
                .map(this::getPautaDTO);
    }

    private PautaDTO getPautaDTO(final Pauta pauta) {
        return PautaDTO.builder()
                .id(pauta.getId())
                .nome(pauta.getNome())
                .descricao(pauta.getDescricao())
                .build();
    }
}