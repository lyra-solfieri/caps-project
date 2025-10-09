package com.lyra.caps.controller;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.service.IPacienteService;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDto> criarPaciente(@Valid @RequestBody PacienteDto pacienteDto) {
        try{
            PacienteResponseDto response = pacienteService.criarPaciente(pacienteDto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } 
        catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao cadastrar paciente", e);
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<PacienteResponseDto> cadastrarComOCR(@Valid @RequestBody PacienteDto pacienteDto) {
        try {
            /*
             * Aqui voce pode fazer o OCR e preencher os dados do pacienteDto
             */
            return ResponseEntity.ok(pacienteService.criarPaciente(pacienteDto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDto>> listarTodos() {
        
        try {
            return ResponseEntity.ok(pacienteService.listarTodos());
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