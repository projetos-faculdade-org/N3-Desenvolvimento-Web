package com.oficina.service;

import com.oficina.dto.OrdemServicoRequestDTO;
import com.oficina.dto.OrdemServicoResponseDTO;
import com.oficina.exception.ResourceNotFoundException;
import com.oficina.model.Cliente;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOrdem;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepository repository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private OrdemServicoService service;

    private Cliente clienteExemplo;
    private OrdemServico ordemExemplo;
    private OrdemServicoRequestDTO requestExemplo;

    @BeforeEach
    void setUp() {
        clienteExemplo = Cliente.builder()
                .id(1L)
                .nome("João Silva")
                .cpf("123.456.789-00")
                .telefone("(11) 99999-9999")
                .email("joao@email.com")
                .build();

        ordemExemplo = OrdemServico.builder()
                .id(1L)
                .cliente(clienteExemplo)
                .veiculo("Honda Civic 2020")
                .problema("Troca de óleo")
                .status(StatusOrdem.ABERTO)
                .valor(new BigDecimal("150.00"))
                .build();

        requestExemplo = OrdemServicoRequestDTO.builder()
                .clienteId(1L)
                .veiculo("Honda Civic 2020")
                .problema("Troca de óleo")
                .status(StatusOrdem.ABERTO)
                .valor(new BigDecimal("150.00"))
                .build();
    }

    @Test
    void deveCriarOrdemComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExemplo));
        when(repository.save(any(OrdemServico.class))).thenReturn(ordemExemplo);
        when(clienteService.toDTO(clienteExemplo)).thenCallRealMethod();

        OrdemServicoResponseDTO resultado = service.criar(requestExemplo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(StatusOrdem.ABERTO, resultado.getStatus());
        verify(repository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    void deveBuscarOrdemPorIdComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(ordemExemplo));
        when(clienteService.toDTO(clienteExemplo)).thenCallRealMethod();

        OrdemServicoResponseDTO resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Honda Civic 2020", resultado.getVeiculo());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.buscarPorId(99L));

        verify(repository, times(1)).findById(99L);
    }

    @Test
    void deveListarTodasAsOrdens() {
        Cliente cliente2 = Cliente.builder()
                .id(2L).nome("Maria Souza").cpf("987.654.321-00")
                .telefone("(11) 88888-8888").build();

        OrdemServico outraOrdem = OrdemServico.builder()
                .id(2L)
                .cliente(cliente2)
                .veiculo("Fiat Uno 2018")
                .problema("Freios")
                .status(StatusOrdem.EM_ANDAMENTO)
                .valor(new BigDecimal("300.00"))
                .build();

        when(repository.findAll()).thenReturn(List.of(ordemExemplo, outraOrdem));
        when(clienteService.toDTO(any(Cliente.class))).thenCallRealMethod();

        List<OrdemServicoResponseDTO> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveAtualizarOrdemComSucesso() {
        OrdemServico ordemAtualizada = OrdemServico.builder()
                .id(1L)
                .cliente(clienteExemplo)
                .veiculo("Honda Civic 2020")
                .problema("Troca de óleo e filtro")
                .status(StatusOrdem.EM_ANDAMENTO)
                .valor(new BigDecimal("250.00"))
                .build();

        OrdemServicoRequestDTO requestAtualizado = OrdemServicoRequestDTO.builder()
                .clienteId(1L)
                .veiculo("Honda Civic 2020")
                .problema("Troca de óleo e filtro")
                .status(StatusOrdem.EM_ANDAMENTO)
                .valor(new BigDecimal("250.00"))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(ordemExemplo));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExemplo));
        when(repository.save(any(OrdemServico.class))).thenReturn(ordemAtualizada);
        when(clienteService.toDTO(clienteExemplo)).thenCallRealMethod();

        OrdemServicoResponseDTO resultado = service.atualizar(1L, requestAtualizado);

        assertNotNull(resultado);
        assertEquals(StatusOrdem.EM_ANDAMENTO, resultado.getStatus());
        assertEquals(new BigDecimal("250.00"), resultado.getValor());
        verify(repository, times(1)).save(any(OrdemServico.class));
    }

    @Test
    void deveDeletarOrdemComSucesso() {
        when(repository.findById(1L)).thenReturn(Optional.of(ordemExemplo));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deletar(1L));

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deveBuscarOrdensPorStatus() {
        Cliente cliente2 = Cliente.builder()
                .id(2L).nome("Maria Souza").cpf("987.654.321-00")
                .telefone("(11) 88888-8888").build();

        OrdemServico outraOrdem = OrdemServico.builder()
                .id(2L)
                .cliente(cliente2)
                .veiculo("Fiat Uno 2018")
                .problema("Freios")
                .status(StatusOrdem.ABERTO)
                .valor(new BigDecimal("200.00"))
                .build();

        when(repository.findByStatus(StatusOrdem.ABERTO))
                .thenReturn(List.of(ordemExemplo, outraOrdem));

        List<OrdemServicoResponseDTO> resultado = service.buscarPorStatus(StatusOrdem.ABERTO);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(o -> o.getStatus() == StatusOrdem.ABERTO));
        verify(repository, times(1)).findByStatus(StatusOrdem.ABERTO);
    }
}
