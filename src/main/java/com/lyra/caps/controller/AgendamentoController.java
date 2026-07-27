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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'EDITOR')")
public class AgendamentoController {

    private final IAgendamentoService agendamentoService;

    @PostMapping
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
    public ResponseEntity<List<AgendamentoResponseDto>> listarAgendamentos() {
        try {
            return ResponseEntity.ok(agendamentoService.listarAgendamentos());
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar agendamentos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<AgendamentoResponseDto> listarAgendamentosPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agendamentoService.buscarPorId(id));
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertar/{id}")
    public ResponseEntity<String> alertarAgendamento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(agendamentoService.alertarAgendamento(id));
            
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao alertar agendamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
