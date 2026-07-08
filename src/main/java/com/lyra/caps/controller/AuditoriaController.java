package com.lyra.caps.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AuditoriaLogResponse;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.service.IAuditoriaService;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final IAuditoriaService auditoriaService;

    public AuditoriaController(IAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditoriaLogResponse>> listarLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) AcaoAuditoria acao,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim) {
        Page<AuditoriaLogResponse> logs = auditoriaService.listarLogs(page, size, acao, entidade, usuario, dataInicio, dataFim);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<String>> listarUsuarios() {
        List<String> usuarios = auditoriaService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/entidades")
    public ResponseEntity<List<String>> listarEntidades() {
        List<String> entidades = auditoriaService.listarEntidades();
        return ResponseEntity.ok(entidades);
    }
}
