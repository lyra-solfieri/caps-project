package com.lyra.caps.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lyra.caps.dto.AnamneseDto;
import com.lyra.caps.service.AnamneseService;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/anamneses")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'EDITOR')")
public class AnamneseController {

    private final AnamneseService anamneseService;

    @PostMapping
    public ResponseEntity<AnamneseDto> save(@RequestBody @Valid AnamneseDto anamneseDto) {
        try {
            AnamneseDto savedAnamnese = anamneseService.save(anamneseDto);
            return new ResponseEntity<>(savedAnamnese, HttpStatus.CREATED);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao salvar anamnese", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnamneseDto> findById(@PathVariable Long id) {
        try {
            AnamneseDto anamnese = anamneseService.findById(id);
            return ResponseEntity.ok(anamnese);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar anamnese", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<AnamneseDto> findByPacienteId(@PathVariable Long pacienteId) {
        try {
            AnamneseDto anamnese = anamneseService.findByPacienteId(pacienteId);
            return ResponseEntity.ok(anamnese);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar anamnese por paciente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnamneseDto> update(
            @PathVariable Long id, 
            @RequestBody @Valid AnamneseDto anamneseDto) {
        try {
            anamneseDto.setId(id);
            AnamneseDto updatedAnamnese = anamneseService.update(id, anamneseDto);
            return ResponseEntity.ok(updatedAnamnese);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao atualizar anamnese", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            anamneseService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar anamnese", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AnamneseDto>> listarAnamneses(Pageable pageable) {
        try {
            List<AnamneseDto> anamneses = anamneseService.listarAnamneses(pageable).getContent();
            return ResponseEntity.ok(anamneses);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar anamneses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/quantidade")
    public ResponseEntity<Long> listarQuantidade() {
        try {            
            Long quantidade = anamneseService.listarQuantidade();
            return ResponseEntity.ok(quantidade);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao contar anamneses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}