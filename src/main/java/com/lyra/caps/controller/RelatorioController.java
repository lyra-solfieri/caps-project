package com.lyra.caps.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.relatorio.IndicadoresDesempenhoDto;
import com.lyra.caps.dto.relatorio.AtendimentoPorProfissionalDto;
import com.lyra.caps.service.IRelatorioService;
import com.lyra.caps.util.LogPersonalizado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class RelatorioController {

    private final IRelatorioService relatorioService;

    @GetMapping("/indicadores")
    @Operation(summary = "Obter indicadores de desempenho")
    @ApiResponse(responseCode = "200", description = "Indicadores retornados")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<IndicadoresDesempenhoDto> obterIndicadores(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        try {
            return ResponseEntity.ok(relatorioService.obterIndicadores(inicio, fim));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao obter indicadores", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/atendimentos/por-profissional")
    @Operation(summary = "Obter atendimentos por profissional")
    @ApiResponse(responseCode = "200", description = "Lista de atendimentos retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<AtendimentoPorProfissionalDto>> obterAtendimentosPorProfissional(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        try {
            return ResponseEntity.ok(relatorioService.obterAtendimentosPorProfissional(inicio, fim));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao obter atendimentos por profissional", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
