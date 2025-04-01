package com.lyra.caps.service;

import com.lyra.caps.model.Paciente;

public interface IPacienteService {

    void criarPaciente(Paciente paciente);
    void delete(Long id);
    void update(Paciente paciente);
    // Iterable<Paciente> findAll();

}
