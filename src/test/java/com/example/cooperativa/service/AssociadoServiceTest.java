package com.example.cooperativa.service;

import com.example.cooperativa.dto.AssociadoResponseDTO;
import com.example.cooperativa.dto.CriarAssociadoDTO;
import com.example.cooperativa.exception.AssociadoExistenteException;
import com.example.cooperativa.model.Associado;
import com.example.cooperativa.repository.AssociadoRepository;
import com.example.cooperativa.util.CPFUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.example.cooperativa.util.CacheAlias.ASSOCIADOS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AssociadoServiceTest {

    @MockBean
    private CPFUtil cpfUtil;

    @MockBean
    private AssociadoRepository associadoRepository;

    @MockBean
    private CacheManager cacheManager;

    @Autowired
    private AssociadoService associadoService;

    private final String validCpf = "12345678901";
    private final Long associadoId = 1L;
    private final CriarAssociadoDTO criarAssociadoDTO = new CriarAssociadoDTO(validCpf);
    private final Associado associado = Associado.builder().id(associadoId).cpf(validCpf).build();

    @BeforeEach
    void setUp() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(ASSOCIADOS)).thenReturn(cache);
    }

    @Test
    void deveCriarAssociadoComSucesso() {
        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(true);
        when(associadoRepository.existsByCpf(criarAssociadoDTO.cpf())).thenReturn(false);
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);

        final AssociadoResponseDTO response = associadoService.criarAssociado(criarAssociadoDTO);

        assertNotNull(response);
        assertEquals(associadoId, response.id());
        assertEquals(validCpf, response.cpf());

        verify(cpfUtil).isCpfValido(criarAssociadoDTO.cpf());
        verify(associadoRepository).existsByCpf(criarAssociadoDTO.cpf());
        verify(associadoRepository).save(any(Associado.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> associadoService.criarAssociado(criarAssociadoDTO));

        assertEquals("CPF inválido: " + criarAssociadoDTO.cpf(), exception.getMessage());

        verify(cpfUtil).isCpfValido(criarAssociadoDTO.cpf());
        verifyNoInteractions(associadoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoJaExiste() {
        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(true);
        when(associadoRepository.existsByCpf(criarAssociadoDTO.cpf())).thenReturn(true);

        final AssociadoExistenteException exception = assertThrows(AssociadoExistenteException.class,
                () -> associadoService.criarAssociado(criarAssociadoDTO));

        assertEquals("Este associado já existe.", exception.getMessage());

        verify(cpfUtil).isCpfValido(criarAssociadoDTO.cpf());
        verify(associadoRepository).existsByCpf(criarAssociadoDTO.cpf());
        verify(associadoRepository, never()).save(any(Associado.class));
    }

    @Test
    void deveInvalidarCacheAoCriarAssociado() {
        when(cpfUtil.isCpfValido(criarAssociadoDTO.cpf())).thenReturn(true);
        when(associadoRepository.existsByCpf(criarAssociadoDTO.cpf())).thenReturn(false);
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);

        final Cache cache = cacheManager.getCache(ASSOCIADOS);
        assertNotNull(cache);

        associadoService.criarAssociado(criarAssociadoDTO);

        verify(cache).clear();
        verify(cpfUtil).isCpfValido(criarAssociadoDTO.cpf());
        verify(associadoRepository).existsByCpf(criarAssociadoDTO.cpf());
        verify(associadoRepository).save(any(Associado.class));
    }

    @Test
    void deveListarTodosOsAssociados() {
        when(associadoRepository.findAll()).thenReturn(List.of(associado));

        final List<AssociadoResponseDTO> responseList = associadoService.listarAssociados();

        assertNotNull(responseList);
        assertEquals(1, responseList.size());

        final AssociadoResponseDTO response = responseList.get(0);
        assertEquals(associadoId, response.id());
        assertEquals(validCpf, response.cpf());

        verify(associadoRepository).findAll();
    }
}