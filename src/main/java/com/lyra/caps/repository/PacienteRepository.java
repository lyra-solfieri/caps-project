package com.lyra.caps.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lyra.caps.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByCpf(String cpf);

}
