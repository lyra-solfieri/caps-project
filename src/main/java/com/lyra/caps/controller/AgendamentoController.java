package com.lyra.caps.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.dto.AgendamentoResponseDto;
import com.lyra.caps.service.IAgendamentoService;
import com.lyra.caps.util.LogPersonalizado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class AgendamentoController {

    private final IAgendamentoService agendamentoService;

    @PostMapping
    @Operation(summary = "Criar agendamento")
    @ApiResponse(responseCode = "200", description = "Agendamento realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Map<String, String>> agendar(
            @Valid @RequestBody AgendamentoRequestDto agendamentoRequest) {

        try {

            agendamentoService.agendar(agendamentoRequest);

            return ResponseEntity.ok(
                Map.of("message", "Agendamento realizado com sucesso!")
            );

        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao criar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    @Operation(summary = "Listar agendamentos")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<AgendamentoResponseDto>> listarAgendamentos() {
        try {
            return ResponseEntity.ok(agendamentoService.listarAgendamentos());
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar agendamentos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento")
    @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Map<String, String>> atualizarAgendamento(
            @PathVariable Long id, 
            @Valid @RequestBody AgendamentoRequestDto agendamentoRequest) {
            
        try {
            agendamentoService.atualizarAgendamento(id, agendamentoRequest);
            
            return ResponseEntity.ok(
                Map.of("message", "Agendamento atualizado com sucesso!")
            );
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao atualizar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar agendamento")
    @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        try {
            agendamentoService.deletarAgendamento(id);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao deletar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    @ApiResponse(responseCode = "200", description = "Agendamento encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<AgendamentoResponseDto> listarAgendamentosPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agendamentoService.buscarPorId(id));
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertar/{id}")
    @Operation(summary = "Alertar agendamento")
    @ApiResponse(responseCode = "200", description = "Alerta gerado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<String> alertarAgendamento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agendamentoService.alertarAgendamento(id));
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao alertar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
