package com.lyra.caps.service;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.mapper.PacienteMapper;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService implements IPacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    @Transactional
    public PacienteResponseDto criarPaciente(PacienteDto pacienteDto) {
        // adicionar funcionalidade de duplicidade de cpf
        
        Paciente paciente = pacienteMapper.toEntity(pacienteDto);
        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        return pacienteMapper.toResponseDto(pacienteSalvo);
    }

    @Override
    public PacienteResponseDto buscarPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return pacienteMapper.toResponseDto(paciente);
    }

    @Override
    public List<PacienteResponseDto> listarTodos() {
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void atualizarPaciente(Long id, PacienteDto pacienteDto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        pacienteMapper.updateEntityFromDto(pacienteDto, paciente);
        pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public void deletarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }
}