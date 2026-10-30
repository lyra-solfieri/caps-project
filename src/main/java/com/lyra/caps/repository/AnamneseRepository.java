package com.lyra.caps.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lyra.caps.model.Anamnese;
import com.lyra.caps.model.Paciente;

public interface AnamneseRepository extends JpaRepository<Anamnese, Long> {

    @Query("select a from Anamnese a left join fetch a.paciente")
    Page<Anamnese> findAll(Pageable pageable);

    @Query("select a from Anamnese a left join fetch a.paciente where a.paciente = ?1")
    Anamnese findByPaciente(Paciente paciente);

}
