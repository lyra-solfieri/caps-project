package com.lyra.caps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lyra.caps.model.Evolucao;

public interface EvolucaoRepository extends JpaRepository<Evolucao, Long> {

    List<Evolucao> findByPacienteId(Long pacienteId);

}
