package com.lyra.caps.controller;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.service.IPacienteService;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;


@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class PacienteController {

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    
    @PostMapping
    public ResponseEntity<?> criarPaciente(@Valid @RequestBody PacienteDto pacienteDto) {
        try {
            PacienteResponseDto salvo = pacienteService.criarPaciente(pacienteDto);
            return ResponseEntity.ok(salvo);
            
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
                    
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao criar paciente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Ocorreu um erro interno no servidor."));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> buscarPacientePorId(@PathVariable Long id) {

        try {
            return ResponseEntity.ok(pacienteService.buscarPorId(id));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<PacienteResponseDto>> listarTodos(
         @RequestParam(defaultValue = "0") int pageNo,
         @RequestParam(defaultValue = "10") int pageSize, 
         @RequestParam(defaultValue = "id") String sortBy,
         @RequestParam(defaultValue = "asc") String direction) {
        
        try {
            Page<PacienteResponseDto> pacientes = pacienteService.listarTodos(pageNo, pageSize);            
            return ResponseEntity.ok(pacientes);

        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar pacientes", e);
            return ResponseEntity.internalServerError().build();
        }
            
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPaciente(@PathVariable Long id, @RequestBody  PacienteDto pacienteDto) {
        try {
            pacienteService.atualizarPaciente(id, pacienteDto);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao atualizar paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        try{
            pacienteService.deletarPaciente(id);
            return ResponseEntity.noContent().build();

        } catch(Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar paciente", e);
            return ResponseEntity.internalServerError().build();

        }
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<PacienteResponseDto>> filtrar(
        @RequestParam (defaultValue = "0") int pageNo,
        @RequestParam (defaultValue = "10") int pageSize,
        @RequestParam (defaultValue = "id") String sortBy,
        @RequestParam (defaultValue = "asc") String direction,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String cpf
        ) {
        try {
            Page<PacienteResponseDto> pacientes = pacienteService.filtrar(pageNo, pageSize, sortBy, direction, nome, cpf);
            return ResponseEntity.ok(pacientes);
        
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao filtrar pacientes", e);
            return ResponseEntity.internalServerError().build();
        }
    
    }
}