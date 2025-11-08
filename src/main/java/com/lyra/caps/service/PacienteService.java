package com.lyra.caps.service;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.mapper.PacienteMapper;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.util.LogPersonalizado;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Transactional
@RequiredArgsConstructor
@Service
public class PacienteService implements IPacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final LogPersonalizado logPersonalizado = LogPersonalizado.getLogger(this.getClass());

    @Override
    public PacienteResponseDto criarPaciente(PacienteDto pacienteDto) {
         try {   
            if( pacienteRepository.existsByCpf(pacienteDto.getCpf())) {
                throw new RuntimeException("CPF ja cadastrado");
            }
            
            Paciente paciente = pacienteMapper.toEntity(pacienteDto);
            Paciente pacienteSalvo = pacienteRepository.save(paciente);
            logPersonalizado.info("Paciente criado com sucesso" + pacienteSalvo);
            return pacienteMapper.toResponseDto(pacienteSalvo);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao cadastrar paciente", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar paciente");
        }
    }

    @Override
    public PacienteResponseDto buscarPorId(Long id) {
        try {
            if (!pacienteRepository.existsById(id)) {
                throw new RuntimeException("Paciente nao encontrado");
            }
            Paciente paciente = pacienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
            return pacienteMapper.toResponseDto(paciente);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao buscar paciente", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar paciente");
        }
    }

    @Override
    public List<PacienteResponseDto> listarTodos() {
        try {
            return pacienteRepository.findAll().stream()
                    .map(pacienteMapper::toResponseDto)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao listar pacientes", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar pacientes");
        }
    }

    @Override    
    public void atualizarPaciente(Long id, PacienteDto pacienteDto) {
        
        try{
            Paciente paciente = pacienteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
            pacienteMapper.updateEntityFromDto(pacienteDto, paciente);
            pacienteRepository.save(paciente);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao atualizar paciente", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar paciente");
        }
    }

    @Override
    public void deletarPaciente(Long id) {
        try { 
                       
            if (!pacienteRepository.existsById(id)) {
                throw new RuntimeException("Paciente nao encontrado");
            }

            pacienteRepository.deleteById(id);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao deletar paciente", e);
            e.printStackTrace();
            throw new RuntimeException("Erro ao deletar paciente");
        }

    }
   
}