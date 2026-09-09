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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/anamneses")
@AllArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class AnamneseController {

    private final AnamneseService anamneseService;

    @PostMapping
    @Operation(summary = "Salvar anamnese")
    @ApiResponse(responseCode = "201", description = "Anamnese criada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Buscar anamnese por ID")
    @ApiResponse(responseCode = "200", description = "Anamnese encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Buscar anamnese por ID do paciente")
    @ApiResponse(responseCode = "200", description = "Anamnese encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Atualizar anamnese")
    @ApiResponse(responseCode = "200", description = "Anamnese atualizada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Deletar anamnese")
    @ApiResponse(responseCode = "204", description = "Anamnese deletada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Listar anamneses")
    @ApiResponse(responseCode = "200", description = "Lista de anamneses retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Contar anamneses")
    @ApiResponse(responseCode = "200", description = "Quantidade de anamneses")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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