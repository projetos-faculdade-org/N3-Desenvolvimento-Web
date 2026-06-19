package com.oficina.service;

import com.oficina.dto.ClienteRequestDTO;
import com.oficina.dto.ClienteResponseDTO;
import com.oficina.exception.ResourceNotFoundException;
import com.oficina.model.Cliente;
import com.oficina.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente cliente = toEntity(dto);
        return toDTO(repository.save(cliente));
    }

    public List<ClienteResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com id: " + id));
        return toDTO(cliente);
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com id: " + id));

        Cliente cliente = toEntity(dto);
        cliente.setId(id);
        return toDTO(repository.save(cliente));
    }

    public void deletar(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com id: " + id));
        repository.deleteById(id);
    }

    // --------------------------------
    // Métodos auxiliares de conversão
    // --------------------------------

    private Cliente toEntity(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .build();
    }

    public ClienteResponseDTO toDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .build();
    }
}
