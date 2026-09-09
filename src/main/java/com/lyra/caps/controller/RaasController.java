package com.lyra.caps.controller;

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

import com.lyra.caps.dto.RaasRequestDTO;
import com.lyra.caps.service.IRaasService;
import com.lyra.caps.util.LogPersonalizado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/raas")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class RaasController {

    private final IRaasService raasService;

    public RaasController(IRaasService raasService) {
        this.raasService = raasService;
    }

    @PostMapping
    @Operation(summary = "Gerar RAAS")
    @ApiResponse(responseCode = "200", description = "RAAS gerado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> criar(@RequestBody RaasRequestDTO dto) {
        return ResponseEntity.ok(raasService.gerarRaas(dto));
    }

    @GetMapping
    @Operation(summary = "Listar RAAS paginado")
    @ApiResponse(responseCode = "200", description = "Lista de RAAS retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<?> listarPaginado(
            @org.springframework.data.web.PageableDefault(size = 5, sort = "dataGeracao", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        try {
            return ResponseEntity.ok(raasService.listarPaginado(pageable));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar RAAS paginado", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar RAAS")
    @ApiResponse(responseCode = "200", description = "RAAS atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody RaasRequestDTO dto) {
        return ResponseEntity.ok(raasService.atualizar(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar RAAS por ID")
    @ApiResponse(responseCode = "200", description = "RAAS encontrado")
    @ApiResponse(responseCode = "404", description = "RAAS não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(raasService.buscarPorId(id));
        } catch (EntityNotFoundException e) {
            LogPersonalizado.getLogger(this.getClass()).warn("RAAS não encontrado: {}" + id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao buscar RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Listar RAAS por paciente")
    @ApiResponse(responseCode = "200", description = "Lista de RAAS retornada")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<?> listar(@PathVariable Long pacienteId) {
        try {
            return ResponseEntity.ok(raasService.listarPorPaciente(pacienteId));
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao listar RAAS por paciente", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar RAAS")
    @ApiResponse(responseCode = "204", description = "RAAS deletado com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
    @Operation(summary = "Gerar PDF do RAAS")
    @ApiResponse(responseCode = "200", description = "PDF gerado com sucesso", content = @Content(mediaType = "application/pdf"))
    @ApiResponse(responseCode = "404", description = "RAAS não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            byte[] pdf = raasService.gerarPdf(id);
            return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=raas_" + id + ".pdf")
                .body(pdf);
        } catch (EntityNotFoundException e) {
            LogPersonalizado.getLogger(this.getClass()).warn("RAAS não encontrado para geração de PDF: {}" + id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao gerar PDF do RAAS", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
