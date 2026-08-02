package com.lyra.caps.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.EvolucaoDto;
import com.lyra.caps.service.EvolucaoService;
import com.lyra.caps.util.LogPersonalizado;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/evolucao")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class EvolucaoController {

    private final EvolucaoService evolucaoService;

    public EvolucaoController(EvolucaoService evolucaoService) {
        this.evolucaoService = evolucaoService;
    }

    @PostMapping
    public ResponseEntity<EvolucaoDto> create(@Valid @RequestBody EvolucaoDto entity) {
        try {
            entity = evolucaoService.save(entity);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao criar evolucao", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvolucaoDto> update(@PathVariable Long id, @Valid @RequestBody EvolucaoDto entity) {
        try {
            entity = evolucaoService.update(id, entity);
            return ResponseEntity.ok(entity);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao atualizar evolucao", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            evolucaoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar evolucao", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvolucaoDto> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(evolucaoService.findById(id));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar evolucao", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EvolucaoDto>> listar(
            @RequestParam(required = false) Long pacienteId,
            Pageable pageable) {
        try {
            if (pacienteId != null) {
                return ResponseEntity.ok(evolucaoService.listarPorPaciente(pacienteId));
            }
            return ResponseEntity.ok(evolucaoService.listar(pageable).getContent());
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar evolucoes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
