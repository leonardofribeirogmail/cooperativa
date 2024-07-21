package com.example.cooperativa.service;

import com.example.cooperativa.dto.AssociadoResponseDTO;
import com.example.cooperativa.dto.CriarAssociadoDTO;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.util.CPFUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociadoServiceTest {

    @Mock
    private CPFUtil cpfUtil;

    @Mock
    private AssociadoRepository associadoRepository;

    @InjectMocks
    private AssociadoService associadoService;

    @Test
    void deveCriarAssociadoComSucesso() {
        final CriarAssociadoDTO criarAssociadoDTO = new CriarAssociadoDTO("12345678901");
        final Associado associado = Associado.builder()
                .id(1L)
                .cpf("12345678901")
                .build();

        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(true);
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);

        final AssociadoResponseDTO response = associadoService.criarAssociado(criarAssociadoDTO);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("12345678901", response.cpf());

        verify(cpfUtil, times(1)).isCpfValido(criarAssociadoDTO.cpf());
        verify(associadoRepository, times(1)).save(any(Associado.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        final CriarAssociadoDTO criarAssociadoDTO = new CriarAssociadoDTO("12345678901");

        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> associadoService.criarAssociado(criarAssociadoDTO));

        assertEquals("CPF inválido: 12345678901", exception.getMessage());

        verify(cpfUtil, times(1)).isCpfValido(criarAssociadoDTO.cpf());
        verify(associadoRepository, times(0)).save(any(Associado.class));
    }

    @Test
    void deveListarTodosOsAssociados() {
        final Associado associado = Associado.builder()
                .id(1L)
                .cpf("12345678901")
                .build();

        when(associadoRepository.findAll()).thenReturn(List.of(associado));

        final List<AssociadoResponseDTO> responseList = associadoService.listarAssociados();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        final AssociadoResponseDTO response = responseList.get(0);
        assertEquals(1L, response.id());
        assertEquals("12345678901", response.cpf());

        verify(associadoRepository, times(1)).findAll();
    }
}