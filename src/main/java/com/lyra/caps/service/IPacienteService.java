package com.lyra.caps.service;


import org.springframework.data.domain.Page;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;

public interface IPacienteService {
    PacienteResponseDto criarPaciente(PacienteDto pacienteDto);
    PacienteResponseDto buscarPorId(Long id);
    Page<PacienteResponseDto> listarTodos(int pageNo, int pagesize);
    void atualizarPaciente(Long id, PacienteDto pacienteDto);
    void deletarPaciente(Long id);
    Page<PacienteResponseDto> filtrar(int pageNo, int pageSize, String sortBy, String direction, String nome, String cpf);
}
