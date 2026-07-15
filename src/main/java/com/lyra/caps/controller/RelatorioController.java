package com.lyra.caps.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.dto.relatorio.IndicadoresDesempenhoDto;
import com.lyra.caps.dto.relatorio.AtendimentoPorProfissionalDto;
import com.lyra.caps.service.IRelatorioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final IRelatorioService relatorioService;

    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresDesempenhoDto> obterIndicadores(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        return ResponseEntity.ok(relatorioService.obterIndicadores(inicio, fim));
    }

    @GetMapping("/atendimentos/por-profissional")
    public ResponseEntity<List<AtendimentoPorProfissionalDto>> obterAtendimentosPorProfissional(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {
        return ResponseEntity.ok(relatorioService.obterAtendimentosPorProfissional(inicio, fim));
    }
}
