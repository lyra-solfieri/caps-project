package com.lyra.caps.service;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.mapper.PacienteMapper;
import com.lyra.caps.model.Paciente;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.repository.PacienteRepository;
import com.lyra.caps.util.LogPersonalizado;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@RequiredArgsConstructor
@Service
public class PacienteService implements IPacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;
    private final AuditoriaService auditoriaService;
    private final UserService userService;
    private final LogPersonalizado logPersonalizado = LogPersonalizado.getLogger(this.getClass());


    @Override
    public PacienteResponseDto criarPaciente(PacienteDto pacienteDto) {
        
        if(pacienteRepository.existsByCpf(pacienteDto.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este CPF já está cadastrado no sistema.");
        }
        
        try {   
            Paciente paciente = Paciente.fromDto(pacienteDto);
            Paciente pacienteSalvo = pacienteRepository.save(paciente);

            auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.CRIACAO, "Paciente", pacienteSalvo.getId(),
                    "Paciente criado: " + pacienteSalvo.getNome(), null);
            
            return PacienteResponseDto.fromEntity(pacienteSalvo);
            
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este CPF já está cadastrado no sistema.");
            
        } catch (Exception e) {
            logPersonalizado.error("Erro interno ao cadastrar paciente", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao cadastrar paciente.");
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
            throw new RuntimeException("Erro ao buscar paciente");
        }
    }

    @Override
    public Page<PacienteResponseDto> listarTodos(int pageNo, int pagesize) {
        try {
            Page <Paciente> pacienPage = pacienteRepository.findAll(PageRequest.of(pageNo, pagesize)); 
            return pacienPage.map(paciente ->{
                PacienteResponseDto pacienteResponseDto = PacienteResponseDto.fromEntity(paciente);
                return pacienteResponseDto;
            });
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao listar pacientes", e);
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

            auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.ATUALIZACAO, "Paciente", id,
                    "Paciente atualizado: " + paciente.getNome(), null);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao atualizar paciente", e);
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

            auditoriaService.registrar(userService.getCurrentUser().getEmail(), AcaoAuditoria.EXCLUSAO, "Paciente", id,
                    "Paciente excluido - ID: " + id, null);
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao deletar paciente", e);
            throw new RuntimeException("Erro ao deletar paciente");
        }

    }

    @Override
    public Page<PacienteResponseDto> filtrar(int pageNo, int pageSize, String sortBy, String direction, String nome,
            String cpf) {
        try {
            Page <Paciente> pacientes = pacienteRepository.filtrar(nome, cpf, PageRequest.of(pageNo, pageSize));
            return pacientes.map(paciente ->{
                PacienteResponseDto pacienteResponseDto = PacienteResponseDto.fromEntity(paciente);
                return pacienteResponseDto;
            });
        }
        catch (Exception e) {
            logPersonalizado.error("Erro ao filtrar pacientes", e);
            throw new RuntimeException("Erro ao filtrar pacientes");
        }
    }
   
}