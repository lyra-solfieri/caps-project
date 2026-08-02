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
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pacienteId", required = false) Long pacienteId) {
        try {
            RaasOcrUploadResponse response = raasOcrService.upload(file, pacienteId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro no upload OCR", e);
            return ResponseEntity.internalServerError().body("Erro ao processar upload: " + e.getMessage());
        }
    }

    @GetMapping("/status/{correlationId}")
    public ResponseEntity<?> status(@PathVariable String correlationId) {
        return raasOcrService.consultarStatus(correlationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resultado/{correlationId}")
    public ResponseEntity<?> resultado(@PathVariable String correlationId) {
        return raasOcrService.getResultado(correlationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/aceitar")
    public ResponseEntity<?> aceitar(@RequestBody RaasAceitarDTO dto) {
        try {
            return ResponseEntity.ok(raasAceiteService.aceitar(dto));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            LogPersonalizado.getLogger(this.getClass()).error("Erro ao aceitar RAAS via OCR", e);
            return ResponseEntity.internalServerError().body("Erro ao aceitar RAAS: " + e.getMessage());
        }
    }
}
