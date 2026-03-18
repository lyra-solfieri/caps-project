package com.lyra.caps.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lyra.caps.dto.AnamneseDto;
import com.lyra.caps.service.AnamneseService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/anamneses")
@AllArgsConstructor
public class AnamneseController {

    private final AnamneseService anamneseService;

    @PostMapping
    public ResponseEntity<AnamneseDto> save(@RequestBody @Valid AnamneseDto anamneseDto) {
        log.info("Requisição para salvar nova anamnese para paciente ID: {}", anamneseDto.getPacienteId());
        try {
            AnamneseDto savedAnamnese = anamneseService.save(anamneseDto);
            return new ResponseEntity<>(savedAnamnese, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Erro ao salvar anamnese: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnamneseDto> findById(@PathVariable Long id) {
        log.info("Requisição para buscar anamnese por ID: {}", id);
        try {
            AnamneseDto anamnese = anamneseService.findById(id);
            return ResponseEntity.ok(anamnese);
        } catch (Exception e) {
            log.error("Erro ao buscar anamnese por ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<AnamneseDto> findByPacienteId(@PathVariable Long pacienteId) {
        log.info("Requisição para buscar anamnese por paciente ID: {}", pacienteId);
        try {
            AnamneseDto anamnese = anamneseService.findByPacienteId(pacienteId);
            return ResponseEntity.ok(anamnese);
        } catch (Exception e) {
            log.error("Erro ao buscar anamnese por paciente ID {}: {}", pacienteId, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnamneseDto> update(
            @PathVariable Long id, 
            @RequestBody @Valid AnamneseDto anamneseDto) {
        log.info("Requisição para atualizar anamnese com ID: {}", id);
        try {

            anamneseDto.setId(id);
            AnamneseDto updatedAnamnese = anamneseService.update(id, anamneseDto);
            return ResponseEntity.ok(updatedAnamnese);
        } catch (Exception e) {
            log.error("Erro ao atualizar anamnese ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Requisição para deletar anamnese com ID: {}", id);
        try {
            anamneseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar anamnese ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
}