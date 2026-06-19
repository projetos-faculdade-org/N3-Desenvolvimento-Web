package com.oficina.service;

import com.oficina.dto.OrdemServicoRequestDTO;
import com.oficina.dto.OrdemServicoResponseDTO;
import com.oficina.exception.ResourceNotFoundException;
import com.oficina.model.Cliente;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOrdem;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository repository;
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    public OrdemServicoService(OrdemServicoRepository repository,
                               ClienteRepository clienteRepository,
                               ClienteService clienteService) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.clienteService = clienteService;
    }

    public OrdemServicoResponseDTO criar(OrdemServicoRequestDTO dto) {
        OrdemServico ordem = toEntity(dto);
        return toDTO(repository.save(ordem));
    }

    public List<OrdemServicoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OrdemServicoResponseDTO buscarPorId(Long id) {
        OrdemServico ordem = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordem de serviço não encontrada com id: " + id));
        return toDTO(ordem);
    }

    public OrdemServicoResponseDTO atualizar(Long id, OrdemServicoRequestDTO dto) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordem de serviço não encontrada com id: " + id));

        OrdemServico ordem = toEntity(dto);
        ordem.setId(id);
        return toDTO(repository.save(ordem));
    }

    public void deletar(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ordem de serviço não encontrada com id: " + id));
        repository.deleteById(id);
    }

    public List<OrdemServicoResponseDTO> buscarPorStatus(StatusOrdem status) {
        return repository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --------------------------------
    // Métodos auxiliares de conversão
    // --------------------------------

    private OrdemServico toEntity(OrdemServicoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com id: " + dto.getClienteId()));

        return OrdemServico.builder()
                .cliente(cliente)
                .veiculo(dto.getVeiculo())
                .problema(dto.getProblema())
                .status(dto.getStatus())
                .valor(dto.getValor())
                .build();
    }

    private OrdemServicoResponseDTO toDTO(OrdemServico ordem) {
        return OrdemServicoResponseDTO.builder()
                .id(ordem.getId())
                .cliente(clienteService.toDTO(ordem.getCliente()))
                .veiculo(ordem.getVeiculo())
                .problema(ordem.getProblema())
                .status(ordem.getStatus())
                .valor(ordem.getValor())
                .build();
    }
}
