package com.lyra.caps.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lyra.caps.dto.AnamneseDto;
import com.lyra.caps.service.AnamneseService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/anamneses")
@AllArgsConstructor
public class AnamneseController {

    private final AnamneseService anamneseService;

    @PostMapping
    public ResponseEntity<AnamneseDto> save(@RequestBody AnamneseDto anamneseDto) {
        log.info("Requisição para salvar nova anamnese");
        AnamneseDto savedAnamnese = anamneseService.save(anamneseDto);
        return new ResponseEntity<>(savedAnamnese, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnamneseDto> findById(@PathVariable Long id) {
        log.info("Requisição para buscar anamnese por ID: {}", id);
        AnamneseDto anamnese = anamneseService.findById(id);
        return ResponseEntity.ok(anamnese);
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<AnamneseDto> findByPacienteId(@PathVariable Long pacienteId) {
        log.info("Requisição para buscar anamnese por paciente ID: {}", pacienteId);
        AnamneseDto anamnese = anamneseService.findByPacienteId(pacienteId);
        return ResponseEntity.ok(anamnese);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnamneseDto> update(
            @PathVariable Long id, 
            @RequestBody AnamneseDto anamneseDto) {
        log.info("Requisição para atualizar anamnese com ID: {}", id);
        AnamneseDto updatedAnamnese = anamneseService.update(id, anamneseDto);
        return ResponseEntity.ok(updatedAnamnese);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Requisição para deletar anamnese com ID: {}", id);
        anamneseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}