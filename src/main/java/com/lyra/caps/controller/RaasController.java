package com.lyra.caps.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.lyra.caps.service.IRaasService;

@RestController
@RequestMapping("/api/raas")
public class RaasController {

    private final IRaasService raasService;

    public RaasController(IRaasService raasService) {
        this.raasService = raasService;
    }


    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdf(@PathVariable Long id) {
        try {
            byte[] pdf = raasService.gerarPdf(id);

            return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=raas_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
