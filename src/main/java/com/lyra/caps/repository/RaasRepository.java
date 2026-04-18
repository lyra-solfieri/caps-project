package com.lyra.caps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lyra.caps.model.Raas;

public interface RaasRepository extends JpaRepository<Raas,Long> {
    List<Raas> findByPacienteId(Long pacienteId);
}
