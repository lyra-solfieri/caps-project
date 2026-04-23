package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.service.IRaasService;

@RestController
@RequestMapping("/api/raas")
public class RaasController {

    private final IRaasService raasService;

    public RaasController(IRaasService raasService) {
        this.raasService = raasService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody RaasRequestDTO dto) {
        return ResponseEntity.ok(raasService.gerarRaas(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(raasService.buscarPorId(id));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<?> listar(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(raasService.listarPorPaciente(pacienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        raasService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {

        byte[] pdf = raasService.gerarPdf(id);

        return ResponseEntity.ok()
            .header("Content-Disposition", "inline; filename=raas_" + id + ".pdf")
            .body(pdf);
    }
}
