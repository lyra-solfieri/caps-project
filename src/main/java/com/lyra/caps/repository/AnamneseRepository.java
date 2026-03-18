package com.lyra.caps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lyra.caps.model.Anamnese;
import com.lyra.caps.model.Paciente;

public interface AnamneseRepository extends JpaRepository<Anamnese, Long> {

    @Query("select a from Anamnese a where a.paciente = ?1")
    Anamnese findByPaciente(Paciente paciente);

}
