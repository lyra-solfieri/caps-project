package com.lyra.caps.controller;

import com.lyra.caps.mapper.ApiResponse;
import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.service.IPacienteService;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> criarPaciente(@Valid @RequestBody PacienteDto pacienteDto) {
        try {
            pacienteService.criarPaciente(pacienteDto);

            ApiResponse<Void> response = ApiResponse.sucesso(
                    "Paciente cadastrado com sucesso",
                    HttpStatus.CREATED,
                    null
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao cadastrar paciente: " + e.getMessage(), e);
            e.printStackTrace();

            ApiResponse<Object> erro = ApiResponse.erro(
                    "Erro ao cadastrar paciente",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
                    
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);

        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDto>> listarTodos() {
        
        try {
            List<PacienteResponseDto> pacientes = pacienteService.listarTodos();
            return ResponseEntity.ok(pacientes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
            
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPaciente(@PathVariable Long id, @RequestBody  PacienteDto pacienteDto) {
        try {
            pacienteService.atualizarPaciente(id, pacienteDto);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            e.printStackTrace();
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao atualizar paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) {
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}