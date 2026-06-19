package com.oficina.config;

import com.oficina.model.Cliente;
import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOrdem;
import com.oficina.repository.ClienteRepository;
import com.oficina.repository.OrdemServicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Popula o banco com dados de exemplo na primeira execução.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(ClienteRepository clienteRepository,
                           OrdemServicoRepository ordemRepository) {
        return args -> {
            if (clienteRepository.count() > 0) {
                return;
            }

            Cliente joao = clienteRepository.save(Cliente.builder()
                    .nome("João Silva")
                    .cpf("123.456.789-00")
                    .telefone("(11) 99999-1111")
                    .email("joao.silva@email.com")
                    .build());

            Cliente maria = clienteRepository.save(Cliente.builder()
                    .nome("Maria Souza")
                    .cpf("987.654.321-00")
                    .telefone("(11) 98888-2222")
                    .email("maria.souza@email.com")
                    .build());

            Cliente carlos = clienteRepository.save(Cliente.builder()
                    .nome("Carlos Pereira")
                    .cpf("456.789.123-00")
                    .telefone("(11) 97777-3333")
                    .email("carlos.pereira@email.com")
                    .build());

            ordemRepository.save(OrdemServico.builder()
                    .cliente(joao)
                    .veiculo("Honda Civic 2020")
                    .problema("Troca de óleo e filtros")
                    .status(StatusOrdem.ABERTO)
                    .valor(new BigDecimal("180.00"))
                    .build());

            ordemRepository.save(OrdemServico.builder()
                    .cliente(maria)
                    .veiculo("Fiat Uno 2018")
                    .problema("Revisão do sistema de freios")
                    .status(StatusOrdem.EM_ANDAMENTO)
                    .valor(new BigDecimal("450.00"))
                    .build());

            ordemRepository.save(OrdemServico.builder()
                    .cliente(carlos)
                    .veiculo("Toyota Corolla 2022")
                    .problema("Alinhamento e balanceamento")
                    .status(StatusOrdem.FINALIZADO)
                    .valor(new BigDecimal("220.00"))
                    .build());
        };
    }
}
