package com.example.cooperativa.service;

import com.example.cooperativa.dto.AssociadoResponseDTO;
import com.example.cooperativa.dto.CriarAssociadoDTO;
import com.example.cooperativa.exception.AssociadoExistenteException;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.util.CPFUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.cooperativa.util.CacheAlias.ASSOCIADOS;

@Service
@RequiredArgsConstructor
public class AssociadoService {

    private final CPFUtil cpfUtil;
    private final AssociadoRepository associadoRepository;

    @CacheEvict(value = ASSOCIADOS, allEntries = true)
    public AssociadoResponseDTO criarAssociado(final CriarAssociadoDTO criarAssociadoDTO) {
        if (!cpfUtil.isCpfValido(criarAssociadoDTO.cpf())) {
            throw new IllegalArgumentException("CPF inválido: " + criarAssociadoDTO.cpf());
        }

        final Associado associado = Associado.builder()
                .cpf(criarAssociadoDTO.cpf())
                .build();

        final Associado salvo = getAssociadoSalvo(associado);

        return AssociadoResponseDTO.builder()
                .id(salvo.getId())
                .cpf(salvo.getCpf())
                .build();
    }

    @Cacheable(ASSOCIADOS)
    public List<AssociadoResponseDTO> listarAssociados() {
        return associadoRepository.findAll().stream()
                .map(associado -> AssociadoResponseDTO.builder()
                        .id(associado.getId())
                        .cpf(associado.getCpf())
                        .build())
                .toList();
    }

    private Associado getAssociadoSalvo(final Associado associado) {
        return Optional.of(associado)
                .filter(ass -> !associadoRepository.existsByCpf(ass.getCpf()))
                .map(associadoRepository::save)
                .orElseThrow(() -> new AssociadoExistenteException("Este associado já existe."));
    }
}