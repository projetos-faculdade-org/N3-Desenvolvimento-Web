package com.oficina.repository;

import com.oficina.model.OrdemServico;
import com.oficina.model.StatusOrdem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByStatus(StatusOrdem status);
}
