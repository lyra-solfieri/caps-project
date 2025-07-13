package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.AgendamentoRequestDto;
import com.lyra.caps.service.IAgendamentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final IAgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity agendar(AgendamentoRequestDto agendamentoRequest) {
        try {
            agendamentoService.agendar(agendamentoRequest);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Agendamento realizado com sucesso");
    }

}
