package com.lyra.caps.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lyra.caps.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByCpf(String cpf);

    @Query("select p from Paciente p where p.nome like %:nome% and p.cpf like %:cpf%" )
    Page<Paciente> filtrar(String nome, String cpf, Pageable pageable);

}
