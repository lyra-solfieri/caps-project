package com.lyra.caps.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lyra.caps.dto.AnamneseDto;
import com.lyra.caps.model.Anamnese;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.repository.AnamneseRepository;
import com.lyra.caps.repository.PacienteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnamneseService  implements IAnamneseService {
   
    private final AnamneseRepository anamneseRepository;
    private final PacienteRepository pacienteRepository;

    @Override
    public AnamneseDto save(AnamneseDto anamneseDto) {

        try {

            Paciente paciente = pacienteRepository.findById(anamneseDto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

            Anamnese anamnese = Anamnese.fromDto(anamneseDto);
            anamnese.setPaciente(paciente);
            anamnese = anamneseRepository.save(anamnese);
            return AnamneseDto.fromEntity(anamnese);
            
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao salvar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar anamnese: " + e.getMessage(), e);
        }
        
    }

    @Override
    public AnamneseDto findById(Long id) {
        try {
            Anamnese anamnese = anamneseRepository.findById(id).get();
            return AnamneseDto.fromEntity(anamnese);
        } catch (Exception e) {
            e.printStackTrace(); 
            log.error("Erro ao buscar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar anamnese: " + e.getMessage(), e);
        }

    }

    @Override
    public void delete(Long id) {
        try {
            if (id != null) {
                anamneseRepository.deleteById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao deletar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao deletar anamnese: " + e.getMessage(), e);
        }
    }

    @Override
    public AnamneseDto update(Long id, AnamneseDto anamneseDto) {
        try {
            Anamnese anamnese = Anamnese.fromDto(anamneseDto);
            anamnese.setId(id);
            anamnese = anamneseRepository.save(anamnese);
            return AnamneseDto.fromEntity(anamnese);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao atualizar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao atualizar anamnese: " + e.getMessage(), e);
        }
    }

    @Override
    public AnamneseDto findByPacienteId(Long id) {
        try {

            Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

            Anamnese anamnese = anamneseRepository.findByPaciente(paciente);
            return AnamneseDto.fromEntity(anamnese);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao buscar anamnese por paciente: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar anamnese: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<AnamneseDto> listarAnamneses(Pageable pageable) {
        try {            
            return anamneseRepository.findAll(pageable).map(AnamneseDto::fromEntity);
        } catch (Exception e) {
            log.error("Erro ao buscar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar anamnese: " + e.getMessage(), e);
        }
    }

    @Override
    public Long listarQuantidade() {
        try {
            return anamneseRepository.count();
        } catch (Exception e) {
            log.error("Erro ao buscar anamnese: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar anamnese: " + e.getMessage(), e);
        }
    }
}
