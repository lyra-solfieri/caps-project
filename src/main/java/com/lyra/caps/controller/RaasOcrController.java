package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lyra.caps.dto.RaasAceitarDTO;
import com.lyra.caps.dto.RaasOcrUploadResponse;
import com.lyra.caps.service.RaasAceiteService;
import com.lyra.caps.service.RaasOcrService;
import com.lyra.caps.util.LogPersonalizado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/raas/ocr")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class RaasOcrController {

    private final RaasOcrService raasOcrService;
    private final RaasAceiteService raasAceiteService;

    public RaasOcrController(RaasOcrService raasOcrService,
                             RaasAceiteService raasAceiteService) {
        this.raasOcrService = raasOcrService;
        this.raasAceiteService = raasAceiteService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload de documento para OCR", description = "Envia um PDF ou imagem para o microserviço de OCR extrair os dados do RAAS.")
    @ApiResponse(responseCode = "200", description = "Documento processado, retorna o resultado do OCR")
    @ApiResponse(responseCode = "400", description = "Nenhum arquivo foi enviado ou o arquivo está vazio")
    @ApiResponse(responseCode = "500", description = "Erro ao processar o upload")
    public ResponseEntity<?> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId) {
        LogPersonalizado.getLogger(this.getClass())
            .info(String.format("Recebido upload OCR | pacienteId=%s | nomeArquivo=%s | tamanho=%s | contentType=%s",
                    pacienteId,
                    file != null ? file.getOriginalFilename() : "null",
                    file != null ? file.getSize() : "null",
                    file != null ? file.getContentType() : "null"));

        if (file == null || file.isEmpty()) {
            LogPersonalizado.getLogger(this.getClass())
                .warn("Upload OCR rejeitado: arquivo ausente ou vazio | pacienteId=" + pacienteId);
            return ResponseEntity.badRequest().body(java.util.Map.of(
                    "message", "Nenhum arquivo foi enviado ou o arquivo está vazio. Envie um PDF ou imagem."));
        }
        try {
            RaasOcrUploadResponse response = raasOcrService.upload(file, pacienteId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass())
                .error("Erro no upload OCR | nomeArquivo=" + file.getOriginalFilename()
                        + " | tamanho=" + file.getSize(), e);
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                    "message", "Erro ao processar o upload: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{correlationId}")
    @Operation(summary = "Consultar status do processamento OCR")
    @ApiResponse(responseCode = "200", description = "Status retornado")
    @ApiResponse(responseCode = "404", description = "Processamento não encontrado")
    public ResponseEntity<?> status(@PathVariable String correlationId) {
        return raasOcrService.consultarStatus(correlationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resultado/{correlationId}")
    @Operation(summary = "Obter resultado do processamento OCR")
    @ApiResponse(responseCode = "200", description = "Resultado retornado")
    @ApiResponse(responseCode = "404", description = "Resultado não encontrado")
    public ResponseEntity<?> resultado(@PathVariable String correlationId) {
        return raasOcrService.getResultado(correlationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/aceitar")
    @Operation(summary = "Aceitar RAAS extraído via OCR")
    @ApiResponse(responseCode = "200", description = "RAAS aceito com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<?> aceitar(@RequestBody RaasAceitarDTO dto) {
        try {
            return ResponseEntity.ok(com.lyra.caps.dto.RaasResponseDTO.fromEntity(raasAceiteService.aceitar(dto)));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao aceitar RAAS via OCR", e);
            return ResponseEntity.internalServerError().body("Erro ao aceitar RAAS: " + e.getMessage());
        }
    }
}
