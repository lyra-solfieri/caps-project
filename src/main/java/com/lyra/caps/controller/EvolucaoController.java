package com.lyra.caps.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.EvolucaoDto;
import com.lyra.caps.service.EvolucaoService;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/evolucao")
public class EvolucaoController {

    private final EvolucaoService evolucaoService;

    public EvolucaoController(EvolucaoService evolucaoService) {
        this.evolucaoService = evolucaoService;
    }

    @PostMapping
    public ResponseEntity<EvolucaoDto> save(@RequestBody EvolucaoDto entity) {
        entity = evolucaoService.save(entity);
        return ResponseEntity.ok(entity);            
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evolucaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvolucaoDto> update(@PathVariable Long id, @RequestBody EvolucaoDto entity) {
        entity = evolucaoService.update(id, entity);
        return ResponseEntity.ok(entity);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvolucaoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(evolucaoService.findById(id));
    }


    @GetMapping("/listar")
    public ResponseEntity<List<EvolucaoDto>> listarAnamneses(Pageable pageable) {       
        try {
            List<EvolucaoDto> anamneses = evolucaoService.listarAnamneses(pageable).getContent();
            return ResponseEntity.ok(anamneses);
        } catch (Exception e) {            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }

}
