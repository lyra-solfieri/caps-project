package com.lyra.caps.service;

import java.util.List;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;

public interface IPacienteService {
    PacienteResponseDto criarPaciente(PacienteDto pacienteDto);
    PacienteResponseDto buscarPorId(Long id);
    List<PacienteResponseDto> listarTodos();
    void atualizarPaciente(Long id, PacienteDto pacienteDto);
    void deletarPaciente(Long id);
}
