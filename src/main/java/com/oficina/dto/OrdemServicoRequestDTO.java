package com.oficina.dto;

import com.oficina.model.StatusOrdem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoRequestDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "Veículo é obrigatório")
    private String veiculo;

    @NotBlank(message = "Problema é obrigatório")
    private String problema;

    @NotNull(message = "Status é obrigatório")
    private StatusOrdem status;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;
}
