package com.lyra.caps.service;

import org.springframework.stereotype.Service;

import com.lyra.caps.model.Paciente;
import com.lyra.caps.repository.PacienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {

    private final PacienteRepository pacienteRepository;

    @Override
    public void criarPaciente(Paciente paciente) {
        pacienteRepository.save(paciente);
    }
        

    @Override
    public void delete(Long id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public void update(Paciente paciente) {
        pacienteRepository.save(paciente);
    }

   
}

    

