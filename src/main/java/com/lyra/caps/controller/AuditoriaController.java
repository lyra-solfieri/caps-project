package com.lyra.caps.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AuditoriaLogResponse;
import com.lyra.caps.model.enums.AcaoAuditoria;
import com.lyra.caps.service.IAuditoriaService;
import com.lyra.caps.util.LogPersonalizado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final IAuditoriaService auditoriaService;

    public AuditoriaController(IAuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar logs de auditoria", description = "Lista logs com filtros opcionais. Requer perfil ADMIN.")
    @ApiResponse(responseCode = "200", description = "Logs retornados")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Page<AuditoriaLogResponse>> listarLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) AcaoAuditoria acao,
            @RequestParam(required = false) String entidade,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim) {
        try {
            Page<AuditoriaLogResponse> logs = auditoriaService.listarLogs(page, size, acao, entidade, usuario, dataInicio, dataFim);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar logs de auditoria", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários da auditoria")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<String>> listarUsuarios() {
        try {
            List<String> usuarios = auditoriaService.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar usuarios da auditoria", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/entidades")
    @Operation(summary = "Listar entidades da auditoria")
    @ApiResponse(responseCode = "200", description = "Lista de entidades retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<String>> listarEntidades() {
        try {
            List<String> entidades = auditoriaService.listarEntidades();
            return ResponseEntity.ok(entidades);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar entidades da auditoria", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
