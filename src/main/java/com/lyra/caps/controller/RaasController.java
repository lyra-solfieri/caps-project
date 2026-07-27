package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.service.IRaasService;
import com.lyra.caps.util.LogPersonalizado;

@RestController
@RequestMapping("/api/raas")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'EDITOR')")
public class RaasController {

    private final IRaasService raasService;

    public RaasController(IRaasService raasService) {
        this.raasService = raasService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody RaasRequestDTO dto) {
        try {
            return ResponseEntity.ok(raasService.gerarRaas(dto));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao criar RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(raasService.buscarPorId(id));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listar(@PathVariable Long pacienteId) {
        try {
            return ResponseEntity.ok(raasService.listarPorPaciente(pacienteId));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar RAAS por paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            raasService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/{id}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            byte[] pdf = raasService.gerarPdf(id);
            return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=raas_" + id + ".pdf")
                .body(pdf);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao gerar PDF do RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
